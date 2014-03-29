package org.client.container;

import org.client.container.GameApi.Operation;

import com.google.gwt.json.client.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import static org.client.container.ContainerException.*;

public class Server {
	
  private final String ACCESS_SIGNATURE = "accessSignature";
  private final String PLAYER_IDS = "playerIds";
  private final String GAME_ID = "gameId";
  private final String MATCH_ID = "matchId";
  private final String OPERATIONS = "operations";
  private final String GAME_STATE = "gameState";
  private final String ERROR = "error";
  private final String TURN = "turn";
  private final String SCORE = "score";
  private final String REASON = "reason";
  private final String HISTORY = "history";
  private final String LAST_MOVE = "lastMove";
  private final String WRONG_ACCESS_SIGNATURE = "WRONG_ACCESS_SIGNATURE";
  private final String WRONG_PLAYER_ID = "WRONG_PLAYER_ID";
  private final String JSON_PARSE_ERROR = "JSON_PARSE_ERROR";
  private final String PLAYER_ID = "playerId";
  
  private final JSONSerialization js = new JSONSerialization();
  private final JSONDeserialization jds = new JSONDeserialization();

  private final String url = "smg-server.appspot.com/";
  
  //for optimization
  private String accessSignature;
  private int matchId;
  private Container container;
  private int gameId;
  private List<String> playerIds;
  private String myPlayerId;
  private Server server;
  private List<Operation> move;
	
  public void sendInsertNewMatch(String accessSignature, int gameId, 
      List<String> playerIds, String myPlayerId, Container container) {
	
	//for optimization
	this.server = this;
	this.container = container;
    this.accessSignature = accessSignature;
	this.gameId = gameId;
	this.playerIds = new ArrayList<String>(playerIds);
	this.myPlayerId = myPlayerId;
	
	StringBuilder sb = new StringBuilder(url);
	sb.append("newMatches");
	String integratedUrl = sb.toString();
	
	JSONObject postInfo = new JSONObject();
	postInfo.put(ACCESS_SIGNATURE, new JSONString(accessSignature));
	JSONArray array = new JSONArray();
	int index = 0;
	for(String s : playerIds) {
	  array.set(index++, new JSONString(s));
	}
	postInfo.put(PLAYER_IDS, array);
	postInfo.put(GAME_ID, new JSONNumber(gameId));
	String jsonString = postInfo.toString();
	InsertNewMatchMessageListener listener = new InsertNewMatchMessageListener();
	listener.sendMessage(jsonString, integratedUrl);
  }
  
  public class InsertNewMatchMessageListener {
	private void receivedMessage(String jsonStringMessage) throws 
	    WrongAccessSignatureException, WrongPlayerIdException,
	    JSONParseErrorException, GetInfoException {
	  JSONObject jsonObject = JSONParser.parseStrict(jsonStringMessage).isObject();
	  if(jsonObject != null) {
	    JSONString status = (JSONString) jsonObject.get(ERROR);
	    if(status == null) {
	      JSONNumber matchId = (JSONNumber) jsonObject.get(MATCH_ID);
	      server.matchId = (int) matchId.doubleValue();
	      getMatchInfo();
	    }else {
	      String errorInfo = status.stringValue();
	      switch (errorInfo) {
	        case WRONG_ACCESS_SIGNATURE: {
	          throw new WrongAccessSignatureException();
	        }
	        case WRONG_PLAYER_ID: {
	    	  throw new WrongPlayerIdException();
	        }
	        case JSON_PARSE_ERROR:{
	          throw new JSONParseErrorException();
	        }
	      }
	    }
	  }else {
	    throw new GetInfoException();
	  }
	}
	
//    public native void sendMessage(String jsonStringMessage, String url) /*-{
//  	  var xhr;
//  	  if ($wnd.XMLHttpRequest) {
//  	    xhr = new XMLHttpRequest();
//  	  }else {
//  	    xhr = new ActiveXObject("Microsoft.XMLHTTP");	
//  	  }
//  	  xhr.onreadystatechange = function() {
//  	    this.@org.client.container.Server.InsertNewMatchMessageListener::receivedMessage(Ljava/lang/String;) (xhr.responseText);
//  	  }
//  	  xhr.open("POST", url, true);
//  	  xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
//  	  xhr.send(jsonStringMessage);
//    }-*/; 
    
    public native void sendMessage(String jsonStringMessage, String url) /*-{
      $.ajax({
        url: url, 
        type: 'POST',
        data: jsonStringMessage,
        success: function(data, textStatus, jqXHR) { 
//            console.log(data);
//            if(data["error"] == undefined) {
//                matchId = parseInt(data['matchId']);
//            }
            this.@org.client.container.Server.InsertNewMatchMessageListener::receivedMessage(Ljava/lang/String;) (data);
//            ok(true);
//            start();  
        },
        error: function(jqXHR, textStatus, errorThrown) {
           this.@org.client.container.Server.InsertNewMatchMessageListener::receivedMessage(Ljava/lang/String;) (jqXHR.responseText); 
        }
        });
  }-*/; 
    
  }
	
  public void sendMakeMove(JSONArray operations) {
    this.move = jds.deserializeMove(operations);
	StringBuilder sb = new StringBuilder(url);
	sb.append("matches/")
	  .append(matchId);
	String integratedUrl = sb.toString();
	JSONObject postInfo = new JSONObject();
	postInfo.put(ACCESS_SIGNATURE, new JSONString(accessSignature));
	JSONArray array = new JSONArray();
	int index = 0;
	for(String s : playerIds) {
	  array.set(index++, new JSONString(s));
	}
	postInfo.put(PLAYER_IDS, array);
	postInfo.put(OPERATIONS, operations);
	MakeMoveMessageListener listener = new MakeMoveMessageListener();
	String jsonStringMessage = postInfo.toString();
	listener.sendMessage(jsonStringMessage, integratedUrl);
  }
  
  public class MakeMoveMessageListener {
    private void receivedMessage(String jsonStringMessage) throws
      WrongAccessSignatureException, WrongPlayerIdException,
      JSONParseErrorException, GetInfoException {
      JSONObject jsonObject = JSONParser.parseStrict(jsonStringMessage).isObject();
      if(jsonObject != null) {
        JSONString status = (JSONString) jsonObject.get(ERROR);
        if(status == null) {
          JSONString matchId = (JSONString) jsonObject.get(MATCH_ID);
          JSONObject gameState = (JSONObject) jsonObject.get(GAME_STATE);
          
          List<Operation> lastMove = move;
          Map<String, Object> state = jds.deserializeState(gameState);
          String turn = myPlayerId;
          
          container.updateUi(state, lastMove, turn);
          
        }else {
  	      String errorInfo = status.stringValue();
  	      switch (errorInfo) {
  	        case WRONG_ACCESS_SIGNATURE: {
  	          throw new WrongAccessSignatureException();
  	        }
  	        case WRONG_PLAYER_ID: {
  	    	  throw new WrongPlayerIdException();
  	        }
  	        case JSON_PARSE_ERROR:{
  	          throw new JSONParseErrorException();
  	        }
  	      }
        }
      }else {
        throw new GetInfoException();
      }
    }
    
//    public native void sendMessage(String jsonStringMessage, String url) /*-{
//      var xhr;
//      if($wnd.XMLHttpRequest) {
//        xhr = new XMLHttpRequest();
//      }else {
//        xhr = new ActiveXObject("Microsoft.XMLHTTP");
//      }
//      xhr.onreadystatechange = function() {
//        this.@org.client.container.Server.MakeMoveMessageListener::receivedMessage(Ljava/lang/String;) (xhr.responseText);
//      }
//      xhr.open("POST", url, true);
//      xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
//      xhr.send(jsonStringMessage);
//    }-*/;
    
    public native void sendMessage(String jsonStringMessage, String url) /*-{
    $.ajax({
        url: url, 
        type: "POST",
        data: jsonStringMessage,
        success: function(data, textStatus, jqXHR) {
            this.@org.client.container.Server.MakeMoveMessageListener::receivedMessage(Ljava/lang/String;) (data);
//            console.log(data);
//            ok(true);
//            start();  
        },
        error: function(jqXHR, textStatus, errorThrown) {
            this.@org.client.container.Server.MakeMoveMessageListener::receivedMessage(Ljava/lang/String;) (jqXHR.responseText); 
        }});
  }-*/;
    
  }
  
  public void getMatchInfo() {
	    StringBuilder sb = new StringBuilder(url);
	    sb.append("matches/")
	      .append(matchId)
	      .append("?")
	      .append(ACCESS_SIGNATURE)
	      .append("=")
	      .append("\"")
	      .append(accessSignature)
	      .append("\"")
	      .append("&")
	      .append(PLAYER_ID)
	      .append("=")
	      .append(myPlayerId);
	    String integratedUrl = sb.toString();
	    GetMatchInfoMessageListener listener = new GetMatchInfoMessageListener();
	    listener.sendMessage(integratedUrl);
	  }
  
  public class GetMatchInfoMessageListener {
    private void receivedMessage(String jsonStringMessage) throws
        WrongAccessSignatureException, WrongPlayerIdException,
        JSONParseErrorException, GetInfoException {
      JSONObject jsonObject = JSONParser.parseStrict(jsonStringMessage).isObject();
      if(jsonObject != null) {
        JSONString status = (JSONString) jsonObject.get(ERROR);
        if(status == null) {
          JSONNumber matchId = (JSONNumber) jsonObject.get(MATCH_ID);
          JSONNumber gameId = (JSONNumber) jsonObject.get(GAME_ID);
          JSONArray playerIds = (JSONArray) jsonObject.get(PLAYER_IDS);
          //JSONNumber score = (JSONNumber) jsonObject.get(SCORE);
          //JSONString reason = (JSONString) jsonObject.get(REASON);
          JSONString playerIdThatHasTurn = (JSONString) jsonObject.get(TURN);
          JSONObject history = (JSONObject) jsonObject.get(HISTORY);
          
          String turn = playerIdThatHasTurn.stringValue();
          Map<String, Object> state = jds.deserializeState(history.get(GAME_STATE).isObject());
          List<Operation> lastMove = jds.deserializeMove(history.get(LAST_MOVE).isArray());
          container.updateUi(state, lastMove, turn);
        }else {
  	      String errorInfo = status.stringValue();
  	      switch (errorInfo) {
  	        case WRONG_ACCESS_SIGNATURE: {
  	          throw new WrongAccessSignatureException();
  	        }
  	        case WRONG_PLAYER_ID: {
  	    	  throw new WrongPlayerIdException();
  	        }
  	        case JSON_PARSE_ERROR:{
  	          throw new JSONParseErrorException();
  	        }
  	      }
        }
      }else {
        throw new GetInfoException();
      }
    }
    
//    public native void sendMessage(String url) /*-{
//      var xhr;
//      if($wnd.XMLHttpRequest) {
//        xhr = new XMLHttpRequest();
//      }else {
//      	xhr = new ActiveXObject("Microsoft.XMLHTTP");
//      }
//      xhr.onreadystatechange = function() {
//        this.@org.client.container.Server.GetMatchInfoMessageListener::receivedMessage(Ljava/lang/String;) (xhr.responseText);
//      }
//      xhr.open("GET", url, true);
//      xhr.send();
//    }-*/;
    
    public native void sendMessage(String url) /*-{
     $.ajax({
        url: url, 
        type: "GET",
        success: function(data, textStatus, jqXHR) {
            this.@org.client.container.Server.GetMatchInfoMessageListener::receivedMessage(Ljava/lang/String;) (data);
//            var j = JSON.parse(data);
//            console.log(data);
//            ok(true);
//            start();  
        },
        error: function(jqXHR, textStatus, errorThrown) {
          this.@org.client.container.Server.GetMatchInfoMessageListener::receivedMessage(Ljava/lang/String;) (jqXHR.responseText);
        }});
  }-*/;
  }
}
