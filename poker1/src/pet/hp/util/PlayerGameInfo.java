package pet.hp.util;

import java.util.*;

import pet.hp.*;

public class PlayerGameInfo {
	public final PlayerInfo player;
	public final Game game;
	public int rake = 0;
	/** hands in this game */
	public int hands;
	/** hands where the player won something */
	public int handswon = 0;
	/** amount won and lost */
	public int won = 0;
	public int pip = 0;
	final int[] foldedon;
	/** number of hands that were won at showdown */
	public int handswonshow;
	/** hands that went to showdown and were shown (should be all hands) */
	public int showdown;
	
	// not public
	
	/** action map: int[] { count, amount } */
	final Map<String,int[]> actionTypes = new TreeMap<String,int[]>();
	
	public PlayerGameInfo(PlayerInfo player, Game game) {
		this.player = player;
		this.game = game;
		this.foldedon = new int[HandUtil.getMaxStreets(game.type)];
	}
	
	public void add(Hand h, Seat s) {
		hands++;
		pip += s.pip;
		if (s.showdown) {
			showdown++;
		}
		if (s.won > 0) {
			handswon++;
			won += s.won;
			// overcounts rake if split?
			rake += h.rake;
		}
		if (s.showdown && s.won > 0) {
			handswonshow++;
		}
	}
	
	void add(int street, Action action) {
		int[] typeCount = getActionType(action.type);
		typeCount[0]++;
		typeCount[1] += action.amount;
		//gi.pip += a.amount;
		
		if (action.type.equals("folds")) {
			foldedon[street]++;
		}
	}
	
	
	
	
	
	
	private int[] getActionType(String action) {
		int[] c = actionTypes.get(action);
		if (c == null) {
			actionTypes.put(action, c = new int[2]);
		}
		return c;
	}
	
	// player c/r freq
	// play cbet freq
	// player agr fac
	
	/** aggression factor count or volume */
	public float af(boolean vol) {
		// amount bet+raise / call
		int[] b = getActionType("bets");
		int[] c = getActionType("calls");
		int[] r = getActionType("raises");
		int[] ch = getActionType("checks");
		int i = vol ? 1: 0;
		return (b[i] + r[i] + 0f) / (c[i] + ch[i]);
	}
	
	@Override
	public String toString() {
		return "GameInfo[hands=" + hands + "]";
	}
	public String toLongString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Hands:  %d  Won:  %d\n", hands, handswon));
		sb.append(String.format("Amount won:  %d  Lost:  %d\n", won, pip));
		sb.append("Actions:\n");
		for (Map.Entry<String,int[]> e : actionTypes.entrySet()) {
			int[] c = e.getValue();
			if (c[0] > 0) {
				String act = e.getKey();
				sb.append("  " + act + " times: " + c[0]);
				if (c[1] > 0) {
					sb.append(" amount: " + c[1]);
				}
				sb.append("\n");
			}
		}
		sb.append("Folded on:\n");
		for (int s = 0; s < foldedon.length; s++) {
			if (foldedon[s] > 0) {
				sb.append("  " + HandUtil.getStreetName(game.type, s) + ":  " + foldedon[s] + "\n");
			}
		}
		sb.append("Show downs:  " + showdown + "\n");
		sb.append("Showdown wins:  " + handswonshow + "\n");
		return sb.toString();
	}
}
