package ProtoTinkyWinky;

import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;


/**
 * RobotPlayer is the class that describes your main robot strategy.
 * The run() method inside this class is like your main function: this is what we'll call once your robot
 * is created!
 */
public class RobotPlayer {
    /**
     * We will use this variable to count the number of turns this robot has been alive.
     * You can use static variables like this to save any information you want. Keep in mind that even though
     * these variables are static, in Battlecode they aren't actually shared between your robots.
     */
    static int turnCount = 0;

    /**
     * A random number generator.
     * We will use this RNG to make some random moves. The Random class is provided by the java.util.Random
     * import at the top of this file. Here, we *seed* the RNG with a constant number (6147); this makes sure
     * we get the same sequence of numbers every time this code is run. This is very useful for debugging!
     */
    static final Random rng = new Random(6147);

    /** Array containing all the possible movement directions. */
    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * It is like the main function for your robot. If this method returns, the robot dies!
     *
     * @param rc  The RobotController object. You use it to perform actions from this robot, and to get
     *            information on its current status. Essentially your portal to interacting with the world.
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        // Hello world! Standard output is very useful for debugging.
        // Everything you say here will be directly viewable in your terminal when you run a match!
        //System.out.println("I'm alive");

        // You can also use indicators to save debug notes in replays.
        rc.setIndicatorString("Hello world!");

        while (true) {
            // This code runs during the entire lifespan of the robot, which is why it is in an infinite
            // loop. If we ever leave this loop and return from run(), the robot dies! At the end of the
            // loop, we call Clock.yield(), signifying that we've done everything we want to do.

            turnCount += 1;  // We have now been alive for one more turn!

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode.
            try {
                // The same run() function is called for every robot on your team, even if they are
                // different types. Here, we separate the control depending on the UnitType, so we can
                // use different strategies on different robots. If you wish, you are free to rewrite
                // this into a different control structure!
				rc.setIndicatorString("");
				switch (rc.getType()){
					case SOLDIER -> runSoldier(rc);
					case MOPPER -> runMopper(rc);
					case SPLASHER -> runSplasher(rc);
					default -> runTower(rc);
					}
                }
             catch (GameActionException e) {
                // Oh no! It looks like we did something illegal in the Battlecode world. You should
                // handle GameActionExceptions judiciously, in case unexpected events occur in the game
                // world. Remember, uncaught exceptions cause your robot to explode!
                System.out.println("GameActionException");
                e.printStackTrace();

            } catch (Exception e) {
                // Oh no! It looks like our code tried to do something bad. This isn't a
                // GameActionException, so it's more likely to be a bug in our code.
                System.out.println("Exception");
                e.printStackTrace();

            } finally {
                // Signify we've done everything we want to do, thereby ending our turn.
                // This will make our code wait until the next turn, and then perform this loop again.
                Clock.yield();
            }
            // End of loop: go back to the top. Clock.yield() has ended, so it's time for another turn!
        }

        // Your code should never reach here (unless it's intentional)! Self-destruction imminent...
    }

	public static void spawning(RobotController rc, int mopperCount) throws GameActionException {
		if ((rc.getPaint() >= 300 && rc.getMoney() >= 500) || (rc.getRoundNum() < rc.getPaint())) {
			for (Direction dir : shuffleArray(directions,rng)) {
				MapLocation nextLoc = rc.getLocation().add(dir);
				// If there are less than x moppers near the tower, spawn more moppers
				if ((rc.getType().equals(UnitType.LEVEL_ONE_PAINT_TOWER) || rc.getType().equals(UnitType.LEVEL_TWO_PAINT_TOWER) || rc.getType().equals(UnitType.LEVEL_THREE_PAINT_TOWER)) && mopperCount < 1) {
					if (rc.canBuildRobot(UnitType.MOPPER, nextLoc)) {rc.buildRobot(UnitType.MOPPER, nextLoc);}
				}
				if (turnCount % 3 == 0 && rc.canBuildRobot(UnitType.SPLASHER, nextLoc )){
					rc.buildRobot(UnitType.SPLASHER, nextLoc);
				} else if (turnCount % 2 == 0 && rc.canBuildRobot(UnitType.MOPPER, nextLoc)){
					rc.buildRobot(UnitType.MOPPER, nextLoc);
				}else{
					if(rc.canBuildRobot(UnitType.SOLDIER, nextLoc)){
						rc.buildRobot(UnitType.SOLDIER, nextLoc);
					}
				}
			}
		}
	}

	static Direction[] shuffleArray(Direction[] dirs, Random rnd) {
		for (int i = dirs.length - 1; i > 0; i--) {
		  int index = rnd.nextInt(i + 1);
		  // Simple swap
		  Direction a = dirs[index];
		  dirs[index] = dirs[i];
		  dirs[i] = a;
		}
		return dirs;
	  }

	public static void paintPattern(RobotController rc, MapLocation center, int patternType) throws GameActionException{
		rc.setIndicatorString(Integer.toString(patternType));
		rc.setIndicatorDot(center, 0, 0, 0);
		 int[][] paintTowerPattern = {
            {2, 1, 1, 1, 2},
            {1, 2, 1, 2, 1},
            {1, 1, 0, 1, 1},
            {1, 2, 1, 2, 1},
            {2, 1, 1, 1, 2}
    	};
		int[][] moneyTowerPattern = {
            {1, 2, 2, 2, 1},
            {2, 2, 1, 2, 2},
            {2, 1, 0, 1, 2},
            {2, 2, 1, 2, 2},
            {1, 2, 2, 2, 1}
    	};
		int[][] defenseTowerPattern = {
            {1, 1, 2, 1, 1},
            {1, 2, 2, 2, 1},
            {2, 2, 0, 2, 2},
            {1, 2, 2, 2, 1},
            {1, 1, 2, 1, 1}
    	};
		int[][] SRPPattern = {
            {2, 1, 2, 1, 2},
            {1, 2, 1, 2, 1},
            {2, 1, 1, 1, 2},
            {1, 2, 1, 2, 1},
            {2, 1, 2, 1, 2}
    	};
		int[][] basePattern = {
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0}
    	};

        switch (patternType) {
            case 1 -> basePattern = moneyTowerPattern;
            case 2 -> basePattern = paintTowerPattern;
            case 3 -> basePattern = defenseTowerPattern;
            case 4 -> basePattern = SRPPattern;
            default -> {
            }
        }
		
		MapLocation topLeft = center.translate(-2, 2);

		// Iterate through the pattern
		for (int dy = 0; dy < 5; dy++) {
			for (int dx = 0; dx < 5; dx++) {
				// Skip if no paint needed (0 in pattern)
				if (basePattern[dy][dx] == 0) continue;
				
				// Calculate target location
				MapLocation target = topLeft.translate(dx, -dy);

				if (!rc.canSenseLocation(target)) {
					if(!moveTo(rc,target)) {continue;}
				} else {
					// Get current paint at location
					MapInfo info = rc.senseMapInfo(target);
					PaintType currentPaint = info.getPaint();
					PaintType desiredPaint = (basePattern[dy][dx] == 2) ? 
						PaintType.ALLY_SECONDARY : PaintType.ALLY_PRIMARY;
					
					if (rc.getType() == UnitType.SOLDIER) {
						// If paint doesn't match and we can attack this location
						if (currentPaint != desiredPaint && currentPaint != PaintType.ENEMY_PRIMARY && currentPaint != PaintType.ENEMY_SECONDARY) {
							// Check if we can paint this location
							if (!rc.canAttack(target)) {
								if(!moveTo(rc,target)){continue;}
							}
					
							// Use secondary paint if pattern value is 2, otherwise primary
							if (rc.canAttack(target)) {rc.attack(target, basePattern[dy][dx] == 2);}
							rc.setIndicatorDot(target, 1, 1, 1);
						}
					} else  {
						// If paint doesn't match and we can attack this location
						if (currentPaint != desiredPaint && rc.canAttack(target) && !currentPaint.isAlly() && currentPaint != PaintType.EMPTY) {
							// Check if we can paint this location
							if (!rc.canAttack(target)) {
								if(!moveTo(rc,target)){continue;}
							}
							// Use secondary paint if pattern value is 2, otherwise primary
							if (rc.canAttack(target)) {rc.attack(target, basePattern[dy][dx] == 2);}
							rc.setIndicatorDot(target, 1, 1, 1);
						}
					}
				}
			}
		}
		moveTo(rc, center);
		Boolean canComplete = false;
		switch (patternType) {
            case 1 -> canComplete = rc.canCompleteTowerPattern(UnitType.LEVEL_ONE_MONEY_TOWER, center);
            case 2 -> canComplete = rc.canCompleteTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, center);
            case 3 -> canComplete = rc.canCompleteTowerPattern(UnitType.LEVEL_ONE_DEFENSE_TOWER, center);
            case 4 -> canComplete = rc.canCompleteResourcePattern(center);
            default -> {
            }
        }
		if (canComplete) {
			switch (patternType) {
				case 1 -> rc.completeTowerPattern(UnitType.LEVEL_ONE_MONEY_TOWER, center);
				case 2 -> rc.completeTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, center);
				case 3 -> rc.completeTowerPattern(UnitType.LEVEL_ONE_DEFENSE_TOWER, center);
				case 4 -> rc.completeResourcePattern(center);
				default -> {
				}
			}
		}
    }
    /**
     * Run a single turn for towers.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    public static void runTower(RobotController rc) throws GameActionException{
        // If a tower can be upgraded, upgrade it
		if (rc.getType().equals(UnitType.LEVEL_ONE_PAINT_TOWER) || rc.getType().equals(UnitType.LEVEL_TWO_PAINT_TOWER)) {
			if (rc.canUpgradeTower(rc.getLocation())) {
				rc.upgradeTower(rc.getLocation());
			}
		}
		if (rc.getType().equals(UnitType.LEVEL_ONE_MONEY_TOWER) || rc.getType().equals(UnitType.LEVEL_TWO_MONEY_TOWER)) {
			if (rc.canUpgradeTower(rc.getLocation())) {
				rc.upgradeTower(rc.getLocation());
			}
		}
		if (rc.getType().equals(UnitType.LEVEL_ONE_DEFENSE_TOWER)) {
			if (rc.canUpgradeTower(rc.getLocation())) {
				rc.upgradeTower(rc.getLocation());
			}
		}
		// Track number of moppers near the tower
		RobotInfo[] nearbyRobots = rc.senseNearbyRobots(-1);
		int mopperCount = 0;
		for (RobotInfo aBot: nearbyRobots) {
			if (aBot.type == UnitType.MOPPER) {
				 mopperCount += 1;
			}
		}
		spawning(rc, mopperCount);

		// If there's an enemy in range, AOE attack
		for (RobotInfo aBot: nearbyRobots) {
			if ((rc.getTeam() != aBot.team)) {
				MapLocation attackSpot = aBot.location;
				if (rc.canAttack(attackSpot)) {
				rc.attack(attackSpot);
				rc.attack(null);
				}
			}
		}
    }

	
    /**
     * Run a single turn for a Soldier.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    public static void runSoldier(RobotController rc) throws GameActionException{
        MapLocation here = rc.getLocation();

		// Sense and label nearby robots
		rc.setIndicatorString("Scanning");
        RobotInfo[] nearbyRobots = rc.senseNearbyRobots(-1);
		RobotInfo nearestTower = null;
        int nTowerDist = 9999;
        RobotInfo nearestMopper = null;
        int nMopDist = 9999;
		RobotInfo nearestEnemyTower = null;
		int nETDist = 9999;
		boolean enemies = false;
		int countAlllies = 0;
		MapLocation distantAlly = null;
		int fAllyDist = -1;
        for (RobotInfo aBot : nearbyRobots) {
            int botDist = aBot.location.distanceSquaredTo(here);
			if (rc.getTeam() == aBot.team && botDist < nTowerDist && (aBot.type != UnitType.SOLDIER && aBot.type != UnitType.SPLASHER && aBot.type != UnitType.MOPPER)){
                nTowerDist = botDist;
                nearestTower = aBot;
            }
			if (rc.getTeam() == aBot.team && botDist < nMopDist && aBot.type == UnitType.MOPPER){
                nMopDist = botDist;
                nearestMopper = aBot;
            }
			if (rc.getTeam() != aBot.team && botDist < nETDist && (aBot.type != UnitType.SOLDIER && aBot.type != UnitType.SPLASHER && aBot.type != UnitType.MOPPER)){
                nETDist = botDist;
                nearestEnemyTower = aBot;
            }
			if (rc.getTeam() != aBot.team) {enemies = true;} else if (botDist < 5) {countAlllies ++;}
			if (rc.getTeam() == aBot.team && botDist > fAllyDist) {
				fAllyDist = botDist;
				distantAlly = aBot.location;
			}
        }

		
		MapLocation[] nearbyRuins = rc.senseNearbyRuins(-1);
		MapLocation nearestRuin = null;
		int nRuinDist = 9999;

		for (MapLocation aLoc : nearbyRuins) {
			int ruinDist = here.distanceSquaredTo(aLoc);
			if (rc.senseRobotAtLocation(aLoc) == null && ruinDist < nRuinDist) {
				nearestRuin = aLoc;
				nRuinDist = ruinDist;
			}
		}
		
		// If not enough paint to safely attack/paint, go refill
		if (rc.getPaint() < 105 && nearestTower != null) {
			rc.setIndicatorString("Getting paint");
            refill(rc,nearestTower.location);
        } else if (rc.getPaint() < 105 && nearestMopper != null) {
			rc.setIndicatorString("Following mopper");
            moveTo(rc,nearestMopper.location);
        }

		// If see enemy tower, attack
		// TODO: Shoot and scoot, scoot and shoot
		if (nearestEnemyTower != null) {
			rc.setIndicatorString("Attack!");
			if (rc.canAttack(nearestEnemyTower.location)) {
				rc.attack(nearestEnemyTower.location);
			} else {
				moveTo(rc, nearestEnemyTower.location);
			}
		}

		// Building on ruins
		if (nearestRuin != null) {
			boolean northMark = false;
			boolean southMark = false;
			boolean eastMark = false;
			try {
				northMark = rc.senseMapInfo(nearestRuin.add(Direction.NORTH)).getMark() == PaintType.ALLY_SECONDARY; //Defense
				southMark = rc.senseMapInfo(nearestRuin.add(Direction.SOUTH)).getMark() == PaintType.ALLY_SECONDARY; //Paint
				eastMark = rc.senseMapInfo(nearestRuin.add(Direction.EAST)).getMark() == PaintType.ALLY_SECONDARY; //Money
			} catch (GameActionException e) {}
			boolean anyMark = northMark || southMark || eastMark;
			if (!anyMark) {
				rc.setIndicatorString("Marking");
				try {
					if (enemies) {
						if (here.distanceSquaredTo(nearestRuin.add(Direction.NORTH)) <= 2) {
							rc.mark(nearestRuin.add(Direction.NORTH), true);
							northMark = true;
							anyMark = true;
						} else {moveTo(rc, nearestRuin.add(Direction.NORTH));}
					} else if (rc.getMoney() < rc.getRoundNum()/4) {
						if (here.distanceSquaredTo(nearestRuin.add(Direction.EAST)) <= 2) {
							rc.mark(nearestRuin.add(Direction.EAST), true);
							eastMark = true;
							anyMark = true;
						} else {moveTo(rc, nearestRuin.add(Direction.EAST));}
					} else {
						if (here.distanceSquaredTo(nearestRuin.add(Direction.SOUTH)) <= 2) {
							rc.mark(nearestRuin.add(Direction.SOUTH), true);
							southMark = true;
							anyMark = true;
						} else {moveTo(rc, nearestRuin.add(Direction.SOUTH));}
					}
				} catch (GameActionException e) {}
			}

			if (anyMark) {rc.setIndicatorString("Building");}
			if (northMark) {
				paintPattern(rc, nearestRuin, 3);
			}
			if (southMark) {
				paintPattern(rc, nearestRuin, 2);
			}
			if (eastMark) {
				paintPattern(rc, nearestRuin, 1);
			}
		}



        // Sense information about all visible nearby tiles.
        MapInfo[] nearbyTiles = rc.senseNearbyMapInfos();

		for (MapInfo anInfo : nearbyTiles) {
			if (anInfo.getMark() == PaintType.ALLY_SECONDARY) {
				boolean srp = true;
				for (Direction aDir : directions) {
					if (!rc.canSenseLocation(anInfo.getMapLocation().add(aDir))) {
						if(!moveTo(rc, anInfo.getMapLocation().add(aDir))) {
							srp = false;
							break;
						}
					} else if (rc.senseMapInfo(anInfo.getMapLocation().add(aDir)).hasRuin()) {
						srp = false;
						break;
					}
				}
				if (srp) {
					rc.setIndicatorString("Building");
					paintPattern(rc, anInfo.getMapLocation(), 4);
				}
			}
		}

		for (Direction aDir : directions) {
			if (!rc.onTheMap(here.add(aDir))) {
				rc.setIndicatorString("Stay away from the walls");
				flee(rc, here.add(aDir));
			}
		}

		// if (countAlllies > (rc.getRoundNum() / 400) - 1) {
		// 	if (rc.isMovementReady() && distantAlly != null) {
		// 		rc.setIndicatorString("Disperse");
				flee(rc, distantAlly);
		// 	} else {rc.setIndicatorString("" + Boolean.toString(rc.isMovementReady()) + Boolean.toString(distantAlly != null));}
		// } else {
		// 	rc.setIndicatorString("Wait for Reinforcments " + ((rc.getRoundNum() / 400) - 1));
		// }

        
        // Try to paint beneath us as we walk to avoid paint penalties.
        // Avoiding wasting paint by re-painting our own tiles.
        MapInfo currentTile = rc.senseMapInfo(rc.getLocation());
        if (!currentTile.getPaint().isAlly() && rc.canAttack(rc.getLocation())){
            rc.attack(rc.getLocation());
        }
    }

    public static boolean refill(RobotController rc, MapLocation loc) throws GameActionException {
        // TODO: Follow increasing density to find tower
		if (rc.canTransferPaint(loc, rc.getPaint() - 200)) {
			rc.transferPaint(loc, rc.getPaint() - 200);
			return true;
		} 
		return moveTo(rc, loc);
    }

    /**
     * Run a single turn for a Mopper.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    public static void runMopper(RobotController rc) throws GameActionException{
		//Sense Enemies to Flee
		//RobotInfo[] enemyRobots = rc.senseNearbyRobots(1000, rc.getTeam().opponent());
		RobotInfo[] friendlyRobots = rc.senseNearbyRobots(1000,rc.getTeam());
		if (friendlyRobots.length > 0){
			for(RobotInfo robot:friendlyRobots){
				if((robot.getType().equals(UnitType.SPLASHER) && robot.getPaintAmount() < 200) || (robot.getType().equals(UnitType.SOLDIER) && robot.getPaintAmount() < 105) || (!robot.getType().equals(UnitType.MOPPER) && !robot.getType().equals(UnitType.SOLDIER) && !robot.getType().equals(UnitType.SPLASHER) && robot.getPaintAmount() < 250)){
					int paintAmt;
					if(robot.getType().equals(UnitType.MOPPER) && rc.canTransferPaint(robot.getLocation(), Math.min(100 - robot.getPaintAmount(), Math.min(50, Math.max(rc.getPaint()-50,1))))){
						paintAmt = Math.min(100 - robot.getPaintAmount(), Math.min(50, Math.max(rc.getPaint()-50,1)));
						rc.setIndicatorString("transferring paint to" + robot.location + "amount of paint" + paintAmt+ robot.getType());
						rc.transferPaint(robot.location, paintAmt);
					}
					else if(robot.getType().equals(UnitType.SPLASHER)&& rc.canTransferPaint(robot.getLocation(), Math.min(300 - robot.getPaintAmount(), Math.min(50, Math.max(rc.getPaint()-50,1))))){
						paintAmt = Math.min(300 - robot.getPaintAmount(), Math.min(50, Math.max(rc.getPaint()-50,1)));
						rc.setIndicatorString("transferring paint to" + robot.location + "amount of paint" + paintAmt + robot.getType());
						rc.transferPaint(robot.location, paintAmt);
					}
					else if(robot.getType().equals(UnitType.SOLDIER)&& rc.canTransferPaint(robot.getLocation(), Math.min(200 - robot.getPaintAmount(), Math.min(50, Math.max(rc.getPaint()-50,1))))){
						paintAmt = Math.min(200 - robot.getPaintAmount(), Math.min(50, Math.max(rc.getPaint()-50,1)));
						rc.setIndicatorString("transferring paint to" + robot.location + "amount of paint" + paintAmt+ robot.getType());
						rc.transferPaint(robot.location, paintAmt);
					} else {
						moveTo(rc,robot.getLocation());
					}
				}
			}
		}
	}

    public static void runSplasher(RobotController rc) throws GameActionException{
        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation nextLoc = rc.getLocation().add(dir);
        moveTo(rc,nextLoc);
    }

    public static void updateEnemyRobots(RobotController rc) throws GameActionException{
        // Sensing methods can be passed in a radius of -1 to automatically 
        // use the largest possible value.
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        if (enemyRobots.length != 0){
            rc.setIndicatorString("There are nearby enemy robots! Scary!");
            // Save an array of locations with enemy robots in them for possible future use.
            MapLocation[] enemyLocations = new MapLocation[enemyRobots.length];
            for (int i = 0; i < enemyRobots.length; i++){
                enemyLocations[i] = enemyRobots[i].getLocation();
            }
            RobotInfo[] allyRobots = rc.senseNearbyRobots(-1, rc.getTeam());
            // Occasionally try to tell nearby allies how many enemy robots we see.
            if (rc.getRoundNum() % 20 == 0){
                for (RobotInfo ally : allyRobots){
                    if (rc.canSendMessage(ally.location, enemyRobots.length)){
                        rc.sendMessage(ally.location, enemyRobots.length);
                    }
                }
            }
        }
    }

    public static boolean moveTo(RobotController rc, MapLocation loc) throws GameActionException {
		if (loc != null) {
			return moveUnified(rc, loc, 3);
		} else {return false;}
    }
    
    public static boolean moveTo(RobotController rc, MapLocation loc, int threshold) throws GameActionException {
    	if (loc != null) {
			return moveUnified(rc, loc, threshold);
		} else {return false;}
    }
    
    public static boolean flee(RobotController rc, MapLocation loc) throws GameActionException {
		if (loc != null) {
			MapLocation here = rc.getLocation();
			int dx = loc.x - here.x;
			int dy = loc.y - here.y;
			return moveUnified(rc, new MapLocation(here.x - 2*dx, here.y - 2*dy), 3);
		} else {return false;}
    }
    
    public static boolean flee(RobotController rc, MapLocation loc, int threshold) throws GameActionException {
		if (loc != null) {
			MapLocation here = rc.getLocation();
			int dx = loc.x - here.x;
			int dy = loc.y - here.y;
			return moveUnified(rc, new MapLocation(here.x - 2*dx, here.y - 2*dy), threshold);
		} else {return false;}
    }
    
    public static boolean moveUnified(RobotController rc, MapLocation loc, int threshold) throws GameActionException {
    	if (rc.getLocation().equals(loc) || rc.getMovementCooldownTurns() >= 10) {
    		return false;
    	} else if(rc.getLocation().distanceSquaredTo(loc) > 2) {
    		if (wallRider(rc, loc, threshold)) {
    			return true;
    		} else if (!lookTwoMove(rc, loc)) {
    			return mooTwo(rc, loc);
    		} else {
    			return true;
    		}
    	} else {
    		return mooTwo(rc, loc);
    	}
    }
    
    public static boolean wallRider(RobotController rc, MapLocation loc, int threshold) throws GameActionException {
    	if (threshold == 0) {return false;}    	
    	MapLocation fakeSetPoint = rc.getLocation();
    	int useFakeSetPoint = 0;
    	MapLocation here = rc.getLocation();
    	
    	if (rc.onTheMap(here.add(Direction.NORTH)) && rc.senseMapInfo(here.add(Direction.NORTH)).isWall()) {fakeSetPoint = fakeSetPoint.add(Direction.EAST); useFakeSetPoint += 1;}
    	if (rc.onTheMap(here.add(Direction.NORTHEAST)) && rc.senseMapInfo(here.add(Direction.NORTHEAST)).isWall()) {fakeSetPoint = fakeSetPoint.add(Direction.SOUTHEAST); useFakeSetPoint += 1;}
    	if (rc.onTheMap(here.add(Direction.EAST)) && rc.senseMapInfo(here.add(Direction.EAST)).isWall()) {fakeSetPoint = fakeSetPoint.add(Direction.SOUTH); useFakeSetPoint += 1;}
    	if (rc.onTheMap(here.add(Direction.SOUTHEAST)) && rc.senseMapInfo(here.add(Direction.SOUTHEAST)).isWall()) {fakeSetPoint = fakeSetPoint.add(Direction.SOUTHWEST); useFakeSetPoint += 1;}
    	if (rc.onTheMap(here.add(Direction.SOUTH)) && rc.senseMapInfo(here.add(Direction.SOUTH)).isWall()) {fakeSetPoint = fakeSetPoint.add(Direction.WEST); useFakeSetPoint += 1;}
    	if (rc.onTheMap(here.add(Direction.SOUTHWEST)) && rc.senseMapInfo(here.add(Direction.SOUTHWEST)).isWall()) {fakeSetPoint = fakeSetPoint.add(Direction.NORTHWEST); useFakeSetPoint += 1;}
    	if (rc.onTheMap(here.add(Direction.WEST)) && rc.senseMapInfo(here.add(Direction.WEST)).isWall()) {fakeSetPoint = fakeSetPoint.add(Direction.NORTH); useFakeSetPoint += 1;}
    	if (rc.onTheMap(here.add(Direction.NORTHWEST)) && rc.senseMapInfo(here.add(Direction.NORTHWEST)).isWall()) {fakeSetPoint = fakeSetPoint.add(Direction.NORTHEAST); useFakeSetPoint += 1;}

    	Direction goalDir = here.directionTo(loc);
    	Direction fakeSetPointDir = here.directionTo(fakeSetPoint);
    	if (fakeSetPointDir.rotateRight().equals(goalDir) || fakeSetPointDir.rotateRight().rotateRight().equals(goalDir) || fakeSetPointDir.rotateRight().rotateRight().rotateRight().equals(goalDir)) {
    		return false;
    	}
    	
    	if (useFakeSetPoint >= threshold) {
    		//rc.setIndicatorLine(here, fakeSetPoint, 0, 0, 0);
        	return mooTwo(rc, fakeSetPoint);
    	}
    	return false;
    }
    
    public static boolean lookTwoMove(RobotController rc, MapLocation loc) throws GameActionException {
    	int leastDistanceSquared = 65537;
    	int xOffset = 0;
    	int yOffset = 0;
    	int botX = rc.getLocation().x;
    	int botY = rc.getLocation().y;
    	
    	MapLocation locOffset = new MapLocation(rc.getLocation().x + 0, rc.getLocation().y + 2);
    	int distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = 0;
    		yOffset = 2;
    	}
    	locOffset = new MapLocation(rc.getLocation().x + 1, rc.getLocation().y + 2);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = 1;
    		yOffset = 2;
    	}
    	locOffset = new MapLocation(rc.getLocation().x + 2, rc.getLocation().y + 2);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = 2;
    		yOffset = 2;
    	}
    	locOffset = new MapLocation(rc.getLocation().x + 2, rc.getLocation().y + 1);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = 2;
    		yOffset = 1;
    	}
    	locOffset = new MapLocation(rc.getLocation().x + 2, rc.getLocation().y + 0);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = 2;
    		yOffset = 0;
    	}
    	locOffset = new MapLocation(rc.getLocation().x + 2, rc.getLocation().y - 1);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = 2;
    		yOffset = -1;
    	}
    	locOffset = new MapLocation(rc.getLocation().x + 2, rc.getLocation().y -2);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = 2;
    		yOffset = -2;
    	}
    	locOffset = new MapLocation(rc.getLocation().x + 1, rc.getLocation().y -2);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = 1;
    		yOffset = -2;
    	}
    	locOffset = new MapLocation(rc.getLocation().x + 0, rc.getLocation().y -2);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = 0;
    		yOffset = -2;
    	}
    	locOffset = new MapLocation(rc.getLocation().x -1, rc.getLocation().y -2);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = -1;
    		yOffset = -2;
    	}
    	locOffset = new MapLocation(rc.getLocation().x - 2, rc.getLocation().y -2);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = -2;
    		yOffset = -2;
    	}
    	locOffset = new MapLocation(rc.getLocation().x - 2, rc.getLocation().y -1);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = -2;
    		yOffset = -1;
    	}
    	locOffset = new MapLocation(rc.getLocation().x - 2, rc.getLocation().y + 0);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = -2;
    		yOffset = 0;
    	}
    	locOffset = new MapLocation(rc.getLocation().x - 2, rc.getLocation().y + 1);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = -2;
    		yOffset = 1;
    	}
    	locOffset = new MapLocation(rc.getLocation().x - 2, rc.getLocation().y + 2);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = -2;
    		yOffset = 2;
    	}
    	locOffset = new MapLocation(rc.getLocation().x - 1, rc.getLocation().y + 2);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = -1;
    		yOffset = 2;
    	}
    	
    	if (xOffset == 0 && yOffset == 2) {
    		if (moveN(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset-1,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset+1,botY+yOffset))) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == 1 && yOffset == 2) {
    		if (moveNNE(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset-1,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset+1,botY+yOffset))) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == 2 && yOffset == 2) {
    		if (moveNE(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset-1,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset-1))) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == 2 && yOffset == 1) {
    		if (moveENE(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset+1))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset-1))) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == 2 && yOffset == 0) {
    		if (moveE(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset+1))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset-1))) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == 2 && yOffset == -1) {
    		if (moveESE(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset+1))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset-1))) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == 2 && yOffset == -2) {
    		if (moveSE(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset+1))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset-1,botY+yOffset))) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == 1 && yOffset == -2) {
    		if (moveSSE(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset+1,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset-1,botY+yOffset))) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == 0 && yOffset == -2) {
    		if (moveS(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset+1,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset-1,botY+yOffset))) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == -1 && yOffset == -2) {
    		if (moveSSW(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset+1,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset-1,botY+yOffset))) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == -2 && yOffset == -2) {
    		if (moveSW(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset+1))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset+1,botY+yOffset))) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == -2 && yOffset == -1) {
    		if (moveWSW(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset+1))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset-1))) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == -2 && yOffset == 0) {
    		if (moveW(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset+1))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset-1))) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == -2 && yOffset == 1) {
    		if (moveWNW(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset+1))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset-1))) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == -2 && yOffset == 2) {
    		if (moveNW(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset+1,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset-1))) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == -1 && yOffset == 2) {
    		if (moveNNW(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset+1,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset-1,botY+yOffset))) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else return false;
    	}
    	
    	return false;
    }
    
    public static boolean moveN(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy N");
    	MapLocation lookTwo = rc.getLocation().add(Direction.NORTH).add(Direction.NORTH);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
		if (canFill(rc, rc.getLocation().add(Direction.NORTH))) {fill(rc, rc.getLocation().add(Direction.NORTH));}
    	else if (canFill(rc, rc.getLocation().add(Direction.NORTHEAST))) {fill(rc, rc.getLocation().add(Direction.NORTHEAST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.NORTHWEST))) {fill(rc, rc.getLocation().add(Direction.NORTHWEST));}
    	return moveN2(rc);
    }
    
    public static boolean moveN2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.NORTH)) {
    		rc.move(Direction.NORTH); 
    		return true;
    	} else if (rc.canMove(Direction.NORTHEAST)) {
    		rc.move(Direction.NORTHEAST); 
    		return true;
    	} else if (rc.canMove(Direction.NORTHWEST)) {
    		rc.move(Direction.NORTHWEST); 
    		return true;
    	}
    	return false;
    }
    
    public static boolean moveNNE(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy NNE");
    	MapLocation lookTwo = rc.getLocation().add(Direction.NORTH).add(Direction.NORTHEAST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (canFill(rc, rc.getLocation().add(Direction.NORTH))) {fill(rc, rc.getLocation().add(Direction.NORTH));}
    	else if (canFill(rc, rc.getLocation().add(Direction.NORTHEAST))) {fill(rc, rc.getLocation().add(Direction.NORTHEAST));}
    	return moveNNE2(rc);
    }
    
    public static boolean moveNNE2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.NORTH)) {
    		rc.move(Direction.NORTH); 
    		return true;
    	} else if (rc.canMove(Direction.NORTHEAST)) {
    		rc.move(Direction.NORTHEAST); 
    		return true;
    	}
    	return false;
    }
    
    public static boolean moveNE(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy NE");
    	MapLocation lookTwo = rc.getLocation().add(Direction.NORTHEAST).add(Direction.NORTHEAST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (canFill(rc, rc.getLocation().add(Direction.NORTHEAST))) {fill(rc, rc.getLocation().add(Direction.NORTHEAST));}
    	return moveNE2(rc);
    }
    
    public static boolean moveNE2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.NORTHEAST)) {
    		rc.move(Direction.NORTHEAST); 
    		return true;
    	}
    	return false;
    }
    
    public static boolean moveENE(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy ENE");
    	MapLocation lookTwo = rc.getLocation().add(Direction.EAST).add(Direction.NORTHEAST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (canFill(rc, rc.getLocation().add(Direction.EAST))) {fill(rc, rc.getLocation().add(Direction.EAST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.NORTHEAST))) {fill(rc, rc.getLocation().add(Direction.NORTHEAST));}
    	return moveENE2(rc);
    }
    
    public static boolean moveENE2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.EAST)) {
    		rc.move(Direction.EAST); 
    		return true;
    	} else if (rc.canMove(Direction.NORTHEAST)) {
    		rc.move(Direction.NORTHEAST); 
    		return true;
    	}
    	return false;
    }
    
    public static boolean moveE(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy E");
    	MapLocation lookTwo = rc.getLocation().add(Direction.EAST).add(Direction.EAST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (canFill(rc, rc.getLocation().add(Direction.EAST))) {fill(rc, rc.getLocation().add(Direction.EAST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.NORTHEAST))) {fill(rc, rc.getLocation().add(Direction.NORTHEAST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.SOUTHEAST))) {fill(rc, rc.getLocation().add(Direction.SOUTHEAST));}
    	return moveE2(rc);
    }
    
    public static boolean moveE2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.EAST)) {
    		rc.move(Direction.EAST); 
    		return true;
    	} else if (rc.canMove(Direction.NORTHEAST)) {
    		rc.move(Direction.NORTHEAST); 
    		return true;
    	} else if (rc.canMove(Direction.SOUTHEAST)) {
    		rc.move(Direction.SOUTHEAST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveESE(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy ESE");
    	MapLocation lookTwo = rc.getLocation().add(Direction.EAST).add(Direction.SOUTHEAST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (canFill(rc, rc.getLocation().add(Direction.EAST))) {fill(rc, rc.getLocation().add(Direction.EAST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.SOUTHEAST))) {fill(rc, rc.getLocation().add(Direction.SOUTHEAST));}
    	return moveESE2(rc);
    }
    
    public static boolean moveESE2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.EAST)) {
    		rc.move(Direction.EAST); 
    		return true;
    	} else if (rc.canMove(Direction.SOUTHEAST)) {
    		rc.move(Direction.SOUTHEAST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveSE(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy SE");
    	MapLocation lookTwo = rc.getLocation().add(Direction.SOUTHEAST).add(Direction.SOUTHEAST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (canFill(rc, rc.getLocation().add(Direction.SOUTHEAST))) {fill(rc, rc.getLocation().add(Direction.SOUTHEAST));}
    	return moveSE2(rc);
    }
    
    public static boolean moveSE2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.SOUTHEAST)) {
    		rc.move(Direction.SOUTHEAST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveSSE(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy SSE");
    	MapLocation lookTwo = rc.getLocation().add(Direction.SOUTH).add(Direction.SOUTHEAST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (canFill(rc, rc.getLocation().add(Direction.SOUTH))) {fill(rc, rc.getLocation().add(Direction.SOUTH));}
    	else if (canFill(rc, rc.getLocation().add(Direction.SOUTHEAST))) {fill(rc, rc.getLocation().add(Direction.SOUTHEAST));}
    	return moveSSE2(rc);
    }
    
    public static boolean moveSSE2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.SOUTH)) {
    		rc.move(Direction.SOUTH); 
    		return true;
    	} else if (rc.canMove(Direction.SOUTHEAST)) {
    		rc.move(Direction.SOUTHEAST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveS(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy S");
    	MapLocation lookTwo = rc.getLocation().add(Direction.SOUTH).add(Direction.SOUTH);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (canFill(rc, rc.getLocation().add(Direction.SOUTH))) {fill(rc, rc.getLocation().add(Direction.SOUTH));}
    	else if (canFill(rc, rc.getLocation().add(Direction.SOUTHWEST))) {fill(rc, rc.getLocation().add(Direction.SOUTHWEST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.SOUTHEAST))) {fill(rc, rc.getLocation().add(Direction.SOUTHEAST));}
    	return moveS2(rc);
    }
    
    public static boolean moveS2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.SOUTH)) {
    		rc.move(Direction.SOUTH); 
    		if (canFill(rc, rc.getLocation().add(Direction.SOUTH))) {fill(rc, rc.getLocation().add(Direction.SOUTH));};
    		return true;
    	} else if (rc.canMove(Direction.SOUTHWEST)) {
    		rc.move(Direction.SOUTHWEST); 
    		if (canFill(rc, rc.getLocation().add(Direction.SOUTHEAST))) {fill(rc, rc.getLocation().add(Direction.SOUTHEAST));};
    		return true;
    	} else if (rc.canMove(Direction.SOUTHEAST)) {
    		rc.move(Direction.SOUTHEAST); 
    		if (canFill(rc, rc.getLocation().add(Direction.SOUTHWEST))) {fill(rc, rc.getLocation().add(Direction.SOUTHWEST));};
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveSSW(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy SSW");
    	MapLocation lookTwo = rc.getLocation().add(Direction.SOUTH).add(Direction.SOUTHWEST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (canFill(rc, rc.getLocation().add(Direction.SOUTH))) {fill(rc, rc.getLocation().add(Direction.SOUTH));}
    	else if (canFill(rc, rc.getLocation().add(Direction.SOUTHWEST))) {fill(rc, rc.getLocation().add(Direction.SOUTHWEST));}
    	return moveSSW2(rc);
    }
    
    public static boolean moveSSW2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.SOUTH)) {
    		rc.move(Direction.SOUTH); 
    		return true;
    	} else if (rc.canMove(Direction.SOUTHWEST)) {
    		rc.move(Direction.SOUTHWEST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveSW(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy SW");
    	MapLocation lookTwo = rc.getLocation().add(Direction.SOUTHWEST).add(Direction.SOUTHWEST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (canFill(rc, rc.getLocation().add(Direction.SOUTHWEST))) {fill(rc, rc.getLocation().add(Direction.SOUTHWEST));}
    	return moveSW2(rc);
    }
    
    public static boolean moveSW2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.SOUTHWEST)) {
    		rc.move(Direction.SOUTHWEST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveWSW(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy WSW");
    	MapLocation lookTwo = rc.getLocation().add(Direction.WEST).add(Direction.SOUTHWEST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (canFill(rc, rc.getLocation().add(Direction.WEST))) {fill(rc, rc.getLocation().add(Direction.WEST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.SOUTHWEST))) {fill(rc, rc.getLocation().add(Direction.SOUTHWEST));}
    	return moveWSW2(rc);
    }
    
    public static boolean moveWSW2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.WEST)) {
    		rc.move(Direction.WEST); 
    		return true;
    	} else if (rc.canMove(Direction.SOUTHWEST)) {
    		rc.move(Direction.SOUTHWEST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveW(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy W");
    	MapLocation lookTwo = rc.getLocation().add(Direction.WEST).add(Direction.WEST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (canFill(rc, rc.getLocation().add(Direction.WEST))) {fill(rc, rc.getLocation().add(Direction.WEST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.SOUTHWEST))) {fill(rc, rc.getLocation().add(Direction.SOUTHWEST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.NORTHWEST))) {fill(rc, rc.getLocation().add(Direction.NORTHWEST));}
    	return moveW2(rc);
    }
    
    public static boolean moveW2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.WEST)) {
    		rc.move(Direction.WEST); 
    		return true;
    	} else if (rc.canMove(Direction.SOUTHWEST)) {
    		rc.move(Direction.SOUTHWEST); 
    		return true;
    	} else if (rc.canMove(Direction.NORTHWEST)) {
    		rc.move(Direction.NORTHWEST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveWNW(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy WNW");
    	MapLocation lookTwo = rc.getLocation().add(Direction.WEST).add(Direction.NORTHWEST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (canFill(rc, rc.getLocation().add(Direction.WEST))) {fill(rc, rc.getLocation().add(Direction.WEST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.NORTHWEST))) {fill(rc, rc.getLocation().add(Direction.NORTHWEST));}
    	return moveWNW2(rc);
    }

    public static boolean moveWNW2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.WEST)) {
    		rc.move(Direction.WEST); 
    		return true;
    	} else if (rc.canMove(Direction.NORTHWEST)) {
    		rc.move(Direction.NORTHWEST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveNW(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy NW");
    	MapLocation lookTwo = rc.getLocation().add(Direction.NORTHWEST).add(Direction.NORTHWEST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (canFill(rc, rc.getLocation().add(Direction.NORTHWEST))) {fill(rc, rc.getLocation().add(Direction.NORTHWEST));}
    	return moveNW2(rc);
    }
    
    public static boolean moveNW2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.NORTHWEST)) {
    		rc.move(Direction.NORTHWEST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveNNW(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy NNW");
    	MapLocation lookTwo = rc.getLocation().add(Direction.NORTH).add(Direction.NORTHWEST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (canFill(rc, rc.getLocation().add(Direction.NORTH))) {fill(rc, rc.getLocation().add(Direction.NORTH));}
    	else if (canFill(rc, rc.getLocation().add(Direction.NORTHWEST))) {fill(rc, rc.getLocation().add(Direction.NORTHWEST));}
    	return moveNNW2(rc);
    }
    
    public static boolean moveNNW2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.NORTH)) {
    		rc.move(Direction.NORTH); 
    		return true;
    	} else if (rc.canMove(Direction.NORTHWEST)) {
    		rc.move(Direction.NORTHWEST); 
    		return true;
    	} 
    	return false;
    }

    public static Direction dirSecDir(MapLocation fromLoc, MapLocation toLoc) {
        if (fromLoc == null) {
            return null;
        }

        if (toLoc == null) {
            return null;
        }

        double dx = toLoc.x - fromLoc.x;
        double dy = toLoc.y - fromLoc.y;

        if (Math.abs(dx) >= 2.414 * Math.abs(dy)) {
            if (dx > 0) {
                if (dy > 0) {
                    return Direction.NORTHEAST;
                } else {
                    return Direction.SOUTHEAST;
                }
            } else if (dx < 0) {
                 if (dy > 0) {
                    return Direction.NORTHWEST;
                } else {
                    return Direction.SOUTHWEST;
                }
            } else {
                return Direction.CENTER;
            }
        } else if (Math.abs(dy) >= 2.414 * Math.abs(dx)) {
            if (dy > 0) {
                 if (dx > 0) {
                    return Direction.NORTHEAST;
                } else {
                    return Direction.NORTHWEST;
                }
            } else {
                if (dx > 0) {
                    return Direction.SOUTHEAST;
                } else {
                    return Direction.SOUTHWEST;
                }
            }
        } else {
            if (dy > 0) {
                if (dx > 0) {
                    if (Math.abs(dx) > Math.abs(dy)) {
                        return Direction.EAST;
                    } else {
                        return Direction.NORTH;
                    }
                } else {
                    if (Math.abs(dx) > Math.abs(dy)) {
                        return Direction.WEST;
                    } else {
                        return Direction.NORTH;
                    }
                }
            } else {
                if (dx > 0) {
                    if (Math.abs(dx) > Math.abs(dy)) {
                        return Direction.EAST;
                    } else {
                        return Direction.SOUTH;
                    }
                } else {
                    if (Math.abs(dx) > Math.abs(dy)) {
                        return Direction.WEST;
                    } else {
                        return Direction.SOUTH;
                    }
                }
            }
        }
    }

    public static boolean mooTwo(RobotController rc, MapLocation loc) throws GameActionException {
        Direction dir = rc.getLocation().directionTo(loc);
        if (dir == Direction.CENTER) {
        	int width = rc.getMapWidth();
            int height = rc.getMapHeight();
        	int centerWidth = Math.round(width/2);
            int centerHeight = Math.round(height/2);
            MapLocation centerOfMap = new MapLocation(centerWidth, centerHeight);
        	dir = rc.getLocation().directionTo(centerOfMap);
        }
        Direction secDir = dirSecDir(rc.getLocation(), loc);
        return scoot(rc, dir, secDir, false);
    }
    
    public static boolean mooToo(RobotController rc, MapLocation loc) throws GameActionException {
        Direction dir = rc.getLocation().directionTo(loc);
        if (dir == Direction.CENTER) {
        	int width = rc.getMapWidth();
            int height = rc.getMapHeight();
        	int centerWidth = Math.round(width/2);
            int centerHeight = Math.round(height/2);
            MapLocation centerOfMap = new MapLocation(centerWidth, centerHeight);
        	dir = rc.getLocation().directionTo(centerOfMap);
        }
        Direction secDir = dirSecDir(rc.getLocation(), loc);
        return scoot(rc, dir, secDir, true);
    }
    
    public static boolean scoot(RobotController rc, Direction dir, Direction secDir, boolean restrictive) throws GameActionException {
    	//rc.setIndicatorString("Ultra Greedy " + dir.toString());
    	if (rc.canMove(dir)) {
    		fill(rc, rc.getLocation().add(dir));
    		rc.move(dir);
    		return true;
        } else if (rc.canMove(secDir)) {
        	fill(rc, rc.getLocation().add(secDir));
    		rc.move(secDir);
    		return true;
        } else if (dir.rotateLeft() == secDir) {
        	if (rc.canMove(dir.rotateRight())) {
        		fill(rc, rc.getLocation().add(dir.rotateRight()));
        		rc.move(dir.rotateRight());
        		return true;
        	} else if (restrictive) {
        		return false;
        	} else if (rc.canMove(dir.rotateLeft().rotateLeft())) {
        		fill(rc, rc.getLocation().add(dir.rotateLeft().rotateLeft()));
        		rc.move(dir.rotateLeft().rotateLeft());
        		return true;
        	} else if (rc.canMove(dir.rotateRight().rotateRight())) {
        		fill(rc, rc.getLocation().add(dir.rotateRight().rotateRight()));
        		rc.move(dir.rotateRight().rotateRight());
        		return true;
        	} else if (rc.canMove(dir.rotateLeft().rotateLeft().rotateLeft())) {
        		fill(rc, rc.getLocation().add(dir.rotateLeft().rotateLeft().rotateLeft()));
        		rc.move(dir.rotateLeft().rotateLeft().rotateLeft());
        		return true;
        	} else if (rc.canMove(dir.rotateRight().rotateRight().rotateRight())) {
        		fill(rc, rc.getLocation().add(dir.rotateRight().rotateRight().rotateRight()));
        		rc.move(dir.rotateRight().rotateRight().rotateRight());
        		return true;
        	}
        } else if (rc.canMove(dir.rotateLeft())) {
        	fill(rc, rc.getLocation().add(dir.rotateLeft()));
    		rc.move(dir.rotateLeft());
    		return true;
    	} else if (restrictive) {
    		return false;
    	} else if (rc.canMove(dir.rotateRight().rotateRight())) {
    		fill(rc, rc.getLocation().add(dir.rotateRight().rotateRight()));
    		rc.move(dir.rotateRight().rotateRight());
    		return true;
    	} else if (rc.canMove(dir.rotateLeft().rotateLeft())) {
    		fill(rc, rc.getLocation().add(dir.rotateLeft().rotateLeft()));
    		rc.move(dir.rotateLeft().rotateLeft());
    		return true;
    	} else if (rc.canMove(dir.rotateRight().rotateRight().rotateRight())) {
    		fill(rc, rc.getLocation().add(dir.rotateRight().rotateRight().rotateRight()));
    		rc.move(dir.rotateRight().rotateRight().rotateRight());
    		return true;
    	} else if (rc.canMove(dir.rotateLeft().rotateLeft().rotateLeft())) {
    		fill(rc, rc.getLocation().add(dir.rotateLeft().rotateLeft().rotateLeft()));
    		rc.move(dir.rotateLeft().rotateLeft().rotateLeft());
    		return true;
    	}
    return false;
    }

	public static boolean canFill(RobotController rc, MapLocation loc) throws GameActionException {
		if (rc.canSenseLocation(loc)) {
			MapInfo locInfo = rc.senseMapInfo(loc);
			return rc.canAttack(loc) && locInfo.isPassable() && !locInfo.getPaint().isAlly();
		} else {return false;}
	}

	public static void fill(RobotController rc, MapLocation loc) throws GameActionException {
		if (rc.canSenseLocation(loc)) {
			MapInfo locInfo = rc.senseMapInfo(loc);
			if (rc.canAttack(loc) && locInfo.isPassable() && !locInfo.getPaint().isAlly()) {
				rc.attack(loc);
			}
		}
	}
}
