package org.client.container;

import java.util.List;
import java.util.Map;

import org.client.container.GameApi.Operation;
import org.client.container.GameApi.VerifyMoveDone;
import org.client.container.GameApi.Operation;
import org.client.container.GameApi.Game;
import org.client.container.GameApi.Container;

import org.client.container.GameApi.UpdateUI;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.json.client.*;

import org.client.container.*;


//communicate with game 
public class GameController implements Container{
	private int myPlayerId;
	private final List<Integer> playerIds = Lists.newArrayList();
	private final List<Map<String, Object>> playersInfo = Lists.newArrayList();
	private Map<String, Object> lastState = Maps.newHashMap();
	private int lastMovePlayerId = 0;
	private Map<Integer, Integer> playerIdToNumberOfTokensInPot = Maps.newHashMap();
	
	private org.client.container.Container container = null;
	
	private final Map<String, Integer> convert = Maps.newHashMap();
	
	private Game game;
	private int playerNumber;
	private JSONSerialization js;
	private JSONDeserialization jds;
	
	//private TemporaryServer ts = null;
	
	@Override
    public void sendGameReady() {
	  //forTest("GameController");
	  container.sendGameReady();
	  /*
	  GameReadyMessageListener listener = new GameReadyMessageListener();
	  String GAME_READY = "gameReady";
	  listener.sendMessage(GAME_READY);
	  */
	  
    }
	
	public void updateGame() {
	  container.updateGame();
	}
	

	
	public class GameReadyMessageListener {
		  private void receivedMessage(String jsonStringMessage) {
		    JSONObject messageInJSON = JSONParser.parseStrict(jsonStringMessage).isObject();
			JSONObject stateInJSON = messageInJSON.get("state").isObject();
			JSONArray lastMoveInJSON = messageInJSON.get("lastMove").isArray();
			JSONNumber turnInJSON = messageInJSON.get("turn").isNumber();
			Map<String, Object> state = jds.deserializeState(stateInJSON);
			List<Operation> lastMove = jds.deserializeMove(lastMoveInJSON);
			int turn = (int) turnInJSON.doubleValue();
			UpdateUI updateUI = new UpdateUI(myPlayerId, playersInfo, state, 
			            lastState, lastMove, lastMovePlayerId, 
			    	    playerIdToNumberOfTokensInPot);
			game.sendUpdateUI(updateUI);
			lastState = state;
			lastMovePlayerId = turn;
		  }
			
		  public native void sendMessage(String gameReady) /*-{
		  	var content = gameReady;
		  	window.addEventListener(
		       "message",  
		       function(e) {
		         var val = e.data;
		         this.@org.client.container.GameController.MakeMoveInJSONMessageListener::receivedMessage(Ljava/lang/String;) (JSON.stringify(val));
		       },
		       false);
		    alert("############");
		    $wnd.parent.postMessage(JSON.parse(content), "*");
		    alert("!!!!!!!!!!!!");
		  }-*/;
	}
	
    
	@Override
    public void sendVerifyMoveDone(VerifyMoveDone verifyMoveDone) {
    	
    }
    
	@Override
    public void sendMakeMove(List<Operation> operations) {
		container.sendMakeMove(operations);
      //JSONArray move = js.serializeMove(operations);
      //MakeMoveInJSONMessageListener listener = new MakeMoveInJSONMessageListener();
      //listener.sendMakeMoveInJSON(move);
    }
	
	public class MakeMoveInJSONMessageListener {
	 
	  private void receivedMessage(String jsonStringMessage) {
		JSONObject messageInJSON = JSONParser.parseStrict(jsonStringMessage).isObject();
		JSONObject stateInJSON = messageInJSON.get("state").isObject();
		JSONArray lastMoveInJSON = messageInJSON.get("lastMove").isArray();
		JSONNumber turnInJSON = messageInJSON.get("turn").isNumber();
	    Map<String, Object> state = jds.deserializeState(stateInJSON);
	    List<Operation> lastMove = jds.deserializeMove(lastMoveInJSON);
	    int turn = (int) turnInJSON.doubleValue();
	    UpdateUI updateUI = new UpdateUI(myPlayerId, playersInfo, state, 
	            lastState, lastMove, lastMovePlayerId, 
	    	    playerIdToNumberOfTokensInPot);
	    game.sendUpdateUI(updateUI);
	    lastState = state;
	    lastMovePlayerId = turn;
	  }
		
	  public native void sendMakeMoveInJSON(JSONArray move) /*-{
	    $wnd.parent.postMessage(move, '*');
	    $wnd.addEventListener(
	        'message',  
	        function(e) {
	          var val = e.data;
	          this.@org.cheat.graphics.GameController.MakeMoveInJSONMessageListener::receivedMessage(Ljava/lang/String;) (val);
	        },
	        false);
	  }-*/;
	}
	
	public void updateUi(Map<String, Object> state, List<Operation> lastMove, int turn) {
		UpdateUI updateUI = new UpdateUI(myPlayerId, playersInfo, state, 
	            lastState, lastMove, lastMovePlayerId, 
	    	    playerIdToNumberOfTokensInPot);
		lastMovePlayerId = turn;
		lastState = state;
		game.sendUpdateUI(updateUI);
	}
	
	  public native void forTest(String b) /*-{
		alert(b);
	  }-*/;
     
    public GameController(Game game, int playerNumber, TemporaryServer ts, String myPlayerIdString) {
		convert.put("5754903989321728", 41);
		convert.put("6197337160417280", 42); 
      this.ts = ts;
      container = new org.cheat.graphics.Container(ts, this,myPlayerIdString);
      this.game = game;
      myPlayerId = convert.get(myPlayerIdString);
      this.playerNumber = playerNumber;
      final String PLAYER_ID = "playerId";
      js = new JSONSerialization();
      jds = new JSONDeserialization();
      playerIds.add(41);
      playerIds.add(42);
      for(int i=0; i<playerIds.size(); i++) {
        playersInfo.add(ImmutableMap.<String, Object>of(PLAYER_ID, playerIds.get(i)));
      }
    }
}
