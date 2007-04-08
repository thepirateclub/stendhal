/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.sound.SoundSystem;
import games.stendhal.common.Debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

public abstract class Creature extends RPEntity {

	@Override
	protected void nonCreatureClientAddEventLine(final String text) {
		// no logging for Creature "sounds" in the client window
	}

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(Creature.class);

	// some debug props
	/** should the path be hidden for this creature? */
	public boolean hidePath = false;

	/** display all debug messages for this creature in the game log */
	public boolean watch = false;

	/** the patrolpath */
	private List<Node> patrolPath;

	/** new path to the target */
	private List<Node> targetMovedPath;

	/** the path we got */
	private List<Node> moveToTargetPath;


	protected static String translate(final String type) {
		return "data/sprites/monsters/" + type + ".png";
	}

	public List<Node> getPatrolPath() {
		return patrolPath;
	}

	public List<Node> getTargetMovedPath() {
		return targetMovedPath;
	}

	public List<Node> getMoveToTargetPath() {
		return moveToTargetPath;
	}

	public boolean isPathHidden() {
		return hidePath;
	}


	public List<Node> getPath(final String token) {
		String[] values = token.replace(',', ' ').replace('(', ' ').replace(')', ' ').replace('[', ' ').replace(']',
		        ' ').split(" ");
		List<Node> list = new ArrayList<Node>();

		int x = 0;
		int pass = 1;

		for (String value : values) {
			if (value.trim().length() > 0) {
				int val = Integer.parseInt(value.trim());
				if (pass % 2 == 0) {
					list.add(new Node(x, val));
				} else {
					x = val;
				}
				pass++;
			}
		}

		return list;
	}

	@Override
	public void onChangedAdded(final RPObject base, final RPObject diff) throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);

		// Check if debug is enabled
		if (diff.has("debug") && Debug.CREATURES_DEBUG_CLIENT) {
			patrolPath = null;
			targetMovedPath = null;
			moveToTargetPath = null;

			String debug = diff.get("debug");

			if (watch) {
				StendhalUI.get().addEventLine(getID() + " - " + debug);
			}

			String[] actions = debug.split("\\|");
			// parse all actions
			for (String action : actions) {
				if (action.length() > 0) {
					StringTokenizer tokenizer = new StringTokenizer(action, ";");

					try {
						String token = tokenizer.nextToken();
						logger.debug("- creature action: " + token);
						if (token.equals("sleep")) {
							break;
						} else if (token.equals("patrol")) {
							patrolPath = getPath(tokenizer.nextToken());
						} else if (token.equals("targetmoved")) {
							targetMovedPath = getPath(tokenizer.nextToken());
						} else if (token.equals("movetotarget")) {
							moveToTargetPath = null;
							String nextToken = tokenizer.nextToken();

							if (nextToken.equals("blocked")) {
								nextToken = tokenizer.nextToken();
							}

							if (nextToken.equals("waiting")) {
								nextToken = tokenizer.nextToken();
							}

							if (nextToken.equals("newpath")) {
								moveToTargetPath = null;
								nextToken = tokenizer.nextToken();
								if (nextToken.equals("blocked")) {
									moveToTargetPath = null;
								} else {
									moveToTargetPath = getPath(nextToken);
								}
							}
						}
					} catch (Exception e) {
						logger.warn("error parsing debug string '" + debug + "' actions [" + Arrays.asList(actions)
						        + "] action '" + action + "'", e);
					}
				}
			}
		}
	}

	@Override
	public ActionType defaultAction() {
		return ActionType.ATTACK;
	}

	@Override
	public void onAction(final ActionType at, final String... params) {
		// ActionType at = handleAction(action);
		switch (at) {
			case DEBUG_SHOW_PATH:
				hidePath = false;
				break;
			case DEBUG_HIDE_PATH:
				hidePath = true;
				break;
			case DEBUG_ENABLE_WATCH:
				watch = true;
				break;
			case DEBUG_DISABLE_WATCH:
				watch = false;
				break;
			default:
				super.onAction(at, params);
				break;
		}
	}

	public class Node {

		public int nodeX, nodeY;

		public Node(final int x, final int y) {
			this.nodeX = x;
			this.nodeY = y;
		}
	}

	protected Sprite loadAnimationSprite(final RPObject object) {
		String name = null;

		if (object.has("subclass")) {
			name = object.get("class") + "/" + object.get("subclass");
		} else {
			name = object.get("class");
		}

		SpriteStore store = SpriteStore.get();
		Sprite sprite = store.getSprite("data/sprites/monsters/" + name + ".png");
		return sprite;
	}

	@Override
	protected void buildOfferedActions(List<String> list) {
		super.buildOfferedActions(list);
		if (Debug.CREATURES_DEBUG_CLIENT) {
			if (hidePath) {
	            list.add(ActionType.DEBUG_SHOW_PATH.getRepresentation());
            } else {
	            list.add(ActionType.DEBUG_HIDE_PATH.getRepresentation());
            }
			if (watch) {
				list.add(ActionType.DEBUG_DISABLE_WATCH.getRepresentation());
			} else {
				list.add(ActionType.DEBUG_ENABLE_WATCH.getRepresentation());
			}

		}

	}

	@Override
	public void init(final RPObject object) {

		super.init(object);
		String type = getType();
		if (object.has("name")){
			String name = object.get("name");

			// cyclic sound management
			if (type.startsWith("creature")) {
				if (name.equals("wolf")) {
					SoundSystem.startSoundCycle(this, "wolf-patrol", 40000, 10, 50, 100);
				} else if (name.equals("rat") || name.equals("caverat") || name.equals("venomrat")) {
					SoundSystem.startSoundCycle(this, "rats-patrol", 15000, 10, 30, 80);
				} else if (name.equals("razorrat")) {
					SoundSystem.startSoundCycle(this, "razorrat-patrol", 60000, 10, 50, 75);
				} else if (name.equals("gargoyle")) {
					SoundSystem.startSoundCycle(this, "gargoyle-patrol", 45000, 10, 50, 100);
				} else if (name.equals("boar")) {
					SoundSystem.startSoundCycle(this, "boar-patrol", 30000, 20, 50, 100);
				} else if (name.equals("bear")) {
					SoundSystem.startSoundCycle(this, "bear-patrol", 45000, 30, 80, 75);
				} else if (name.equals("giantrat")) {
					SoundSystem.startSoundCycle(this, "giantrat-patrol", 30000, 30, 60, 65);
				} else if (name.equals("cobra")) {
					SoundSystem.startSoundCycle(this, "cobra-patrol", 60000, 20, 60, 65);
				} else if (name.equals("kobold")) {
					SoundSystem.startSoundCycle(this, "kobold-patrol", 30000, 40, 70, 80);
				} else if (name.equals("goblin")) {
					SoundSystem.startSoundCycle(this, "goblin-patrol", 50000, 30, 85, 65);
				} else if (name.equals("troll")) {
					SoundSystem.startSoundCycle(this, "troll-patrol", 25000, 20, 60, 100);
				} else if (name.equals("orc")) {
					SoundSystem.startSoundCycle(this, "orc-patrol", 45000, 30, 80, 50);
				} else if (name.equals("ogre")) {
					SoundSystem.startSoundCycle(this, "ogre-patrol", 40000, 30, 60, 80);
				} else if (name.equals("skeleton")) {
					SoundSystem.startSoundCycle(this, "skeleton-patrol", 60000, 30, 60, 80);
				} else if (name.equals("cyclops")) {
					SoundSystem.startSoundCycle(this, "cyclops-patrol", 45000, 30, 75, 100);
				}
			}
		}
	}


	//
	// Entity
	//

	/**
	 * Transition method. Create the screen view for this entity.
	 *
	 * @return	The on-screen view of this entity.
	 */
	protected Entity2DView createView() {
		return new Creature2DView(this);
	}
}
