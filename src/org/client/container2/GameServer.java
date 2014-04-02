package org.client.container2;

import java.util.Set;
import java.util.List;


import org.client.container2.GameApi.*;

import com.google.common.collect.Lists;
import com.google.gwt.json.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.appengine.channel.client.ChannelFactory;
import com.google.gwt.appengine.channel.client.Channel;
import com.google.gwt.appengine.channel.client.Socket;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.appengine.channel.client.SocketError;
import com.google.gwt.appengine.channel.client.ChannelFactory.ChannelCreatedCallback;


public class GameServer {

  private final String url = "http://2-dot-smg-container-server2.appspot.com/";
  
  //for optimization
  private String matchId;
  

  private int lastMovePlayerId = -1;
  
  private JSONString accessSignature = null;
  private JSONString gameId = null;
  private JSONArray myLastMove = null;
  private JSONArray playerIds = new JSONArray();
  private GameContainer gameContainer;
  private JSONString myPlayerId = null;
  

  
  
  public GameServer() {
    ServerMessageListener.setServer(this);
  }  
  
  public void closeSocket() {
	  ServerMessageListener.closeSocket();
  }
  
  public JSONArray getPlayerIds() {
    return playerIds;
  }
  
  public String getMyPlayerId() {
    return myPlayerId.stringValue();
  }
  
  public void sendEnterQueue(
	      GameContainer gameContainer,
	      JSONString accessSignature,
	      JSONString myPlayerId,
	      JSONString gameId) {
	this.gameContainer = gameContainer;
	this.accessSignature = accessSignature;
	this.gameId = gameId;
	this.myPlayerId = myPlayerId;
	
	StringBuilder sb = new StringBuilder(url);
	sb.append("queue");
	String integratedUrl = sb.toString();
	
	JSONObject postInfo = new JSONObject();
	postInfo.put("accessSignature", accessSignature);
	postInfo.put("playerId", myPlayerId);
	//postInfo.put("gameId", gameId);
	postInfo.put("gameId", new JSONString("5639274879778816"));
	String message = postInfo.toString();
	
	//*******************************
	//test("Enter in the queue enter in the server");
	//test(gameId.stringValue());
	sendMessage(message, integratedUrl);
  }
  
  private native void sendMessage(String message, String url) /*-{
	var xmlHttp;
	if($wnd.XMLHttpRequest){
	  xmlHttp = new XMLHttpRequest();
	}else{
	  xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlHttp.onreadystatechange = function() {
	  //*****************************
	  //alert(xmlHttp.readyState);
	  //alert(xmlHttp.status);
	  if(xmlHttp.readyState==4 && xmlHttp.status==200) {
	    var response = xmlHttp.responseText;
	    //***********************************
	    //alert(response);
	    //alert("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
	    @org.client.container2.ServerMessageListener::channelBuilder(Ljava/lang/String;) (response);
	  }
	}  
	xmlHttp.open("POST", url, true);
	xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	xmlHttp.send(message);
  }-*/;
  
  
  
  
  
  

  
  
  
  public void sendInsertNewMatch() {
	//test("begin to insert1");
    StringBuilder sb = new StringBuilder(url);
    sb.append("newMatch");
	String integratedUrl = sb.toString();
		
	JSONObject postInfo = new JSONObject();
	//test("begin to insert2");
	postInfo.put("accessSignature", this.accessSignature);
	postInfo.put("playerIds", this.playerIds);
	postInfo.put("gameId", this.gameId);
	//test("begin to insert3");
	String message = postInfo.toString();
	sendMessage1(message, integratedUrl);
  }
  
  public void setMatchId(String matchId) {
    this.matchId = matchId;
  }
	  
  public GameContainer getGameContainer() {
    return gameContainer;
  }
	  
  public void setPlayerIds(JSONArray playerIds) {
    for(int i=0;i<playerIds.size();i++) {
	  this.playerIds.set(i, playerIds.get(i));
	}
  }
	
  public void sendMakeMove(JSONArray operations) {
	//test("I will send the initialMove1");
	myLastMove = operations;
	StringBuilder sb = new StringBuilder(url);
	sb.append("matches/")
	  .append(matchId);
	String integratedUrl = sb.toString();
	JSONObject postInfo = new JSONObject();
	postInfo.put("accessSignature", this.accessSignature);
	postInfo.put("playerIds", this.playerIds);
	postInfo.put("operations", operations);
	String message = postInfo.toString();
	//test("I will send the initialMove2");
	//display(postInfo.toString());
	sendMessage3(message, integratedUrl);
  }
  
  /*
  private native void display(String content) /*-{
    $doc.getElementById("move").innerHTML = content;
  }-*/;
  
  private native void sendMessage3(String message, String url) /*-{
    var xmlHttp;
	if($wnd.XMLHttpRequest){
	  xmlHttp = new XMLHttpRequest();
	}else{
	  xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	//alert("I will send the initialMove message1");
	xmlHttp.onreadystatechange = function() {
	  //*****************************
	  if(xmlHttp.readyState==4 && xmlHttp.status==200) {
	    var response = xmlHttp.responseText;
	    //***********************************
	    //alert(response);
	    @org.client.container2.ServerMessageListener::httpMessageHandler2(Ljava/lang/String;) (response);
	  }
	}  
    xmlHttp.open("POST", url, true);
    xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlHttp.send(message);
    //alert("I will send the initialMove message2");
  }-*/;
  
  public JSONArray getMyLastMove() {
    return myLastMove;
  }
  
  private native void sendMessage1(String message, String url) /*-{
    var xmlHttp;
	if($wnd.XMLHttpRequest){
	  xmlHttp = new XMLHttpRequest();
	}else{
	  xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	} 
    xmlHttp.onreadystatechange = function() {
      if(xmlHttp.readyState==4 && xmlHttp.status==200) {
        var response = xmlHttp.responseText;
        @org.client.container2.ServerMessageListener::httpMessagerHandler(Ljava/lang/String;) (response);
      }
    }  
	//alert("insert a match message send1");
    xmlHttp.open("POST", url, true);
    xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlHttp.send(message);
    //alert("insert a match message send2");
  }-*/;
 

 
  private native void test(String message) /*-{
    $wnd.alert(message);
  }-*/;

}


class ServerMessageListener {
  
  private static GameServer gameServer = null;
  private static boolean signal = false;
  private static ChannelFactory channelFactory;
  private static Channel channel = null;
  private static Socket socket = null;
  
  private static JSONDeserialization jds = new JSONDeserialization();
  private static JSONSerialization js = new JSONSerialization();
  
  
  
  
  public static void setServer(GameServer gameServer) {
    ServerMessageListener.gameServer = gameServer;
  }
  
  public static void channelBuilder(String message) {
	//test("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    JSONObject res = JSONParser.parseStrict(message).isObject();
    if(res.get("error") == null) {
      //test("&&&&&&&&&&&&&&&&&&&&&&&&&");
      JSONString channelTokens_JSON = res.get("channelToken").isString();
      //test("*************************");
      String channelTokens = channelTokens_JSON.stringValue();
      //test("^^^^^^^^^^^^^^^^^^^^^^^^^^");
      JSONValue playerIds_JSON = res.get("playerIds");
      //********************************
      //test(channelTokens);
      /*Build the channel using channelTokens*/
      //test("TTTTTTTTTTTTTTTTTTTTTTT");
      buildChannel(channelTokens, playerIds_JSON);
    }else {
      //test("WWWWWWWWWWWWWWWWWWWWWWW");
      throw new RuntimeException("ERROR!");
    }
  }
  
  public static void buildChannel(String token, final JSONValue playerIds_JSON) {
	//Window.alert("Arrive");
	ChannelFactory.createChannel(token, new ChannelCreatedCallback() {
		@Override
		public void onChannelCreated(final Channel result) {
			
		  socket = result.open(new SocketListener() {
			
			@Override
			public void onOpen() {
			  channel = result;
			  Window.alert("Channel Opened!");
			  if(playerIds_JSON != null) {
			    	//test("receive the playerIds");
			    	signal = true;
			    	gameServer.setPlayerIds(playerIds_JSON.isArray());
			        gameServer.sendInsertNewMatch();
			  }
			}
			
			@Override
			public void onMessage(String message) {
			  channelMessageHandler(message);
			}
			
			@Override
			public void onError(SocketError error) {
				Window.alert("Channel error: " + error.getCode() + " : " + error.getDescription());
			}
			
			@Override
			public void onClose() {
				Window.alert("Channel closed!");
			}
			
		  });
		}
	});
	
	  //Window.alert("Here");
  }
  
  public static void closeSocket() {
    socket.close();
  }
  
  public static void httpMessageHandler2(String message) {
    JSONObject res = JSONParser.parseStrict(message).isObject();
    //test(res.toString());
    if(res.get("error") == null) {
      //test("test keys");
    	Set<String> keys_ = res.keySet();
    	//test(keys_.toString());
      JSONValue state_JSON = res.get("gameState");
      if(state_JSON != null) {
    	JSONObject state = state_JSON.isObject();
    	 //displaying(state.toString());
    	
    	logState(gameServer.getMyPlayerId() + ":" + "\n" + state.toString() + "\n");
    	List<Operation> lastMove_ = Lists.newArrayList();
    	lastMove_.add(new SetTurn(gameServer.getMyPlayerId()));
    	JSONArray lastMove = js.serializeMove(lastMove_);
    	JSONString lastMovePlayerId = new JSONString("1");
    	//test("To update the board using the state returned by the server from POST");
    	gameServer.getGameContainer().updateUi(state, lastMove, lastMovePlayerId, gameServer.getPlayerIds());
      }
    }
  }
  
  public static native void logState(String message) /*-{
    $wnd.parent.frames[2].postMessage(message, '*');
  }-*/;
  
  public static native void displaying(String content) /*-{
	$doc.getElementById("state1").innerHTML = content;
  }-*/;
  
  public static void httpMessagerHandler(String message) {
    JSONObject res = JSONParser.parseStrict(message).isObject();
    JSONString matchId = res.get("matchId").isString();
    gameServer.setMatchId(matchId.stringValue());
    //test("To update the board using the empty state");
    gameServer.getGameContainer().updateUi(new JSONObject(), new JSONArray(), new JSONString("1"), gameServer.getPlayerIds());
  }
  
  public static void channelMessageHandler(String message) {
    JSONObject response = JSONParser.parseStrict(message).isObject();
    if(response.get("error") == null) {
      JSONValue matchId = response.get("matchId");
      JSONValue playerIds = response.get("playerIds");
      if(matchId != null && playerIds != null) {
    	//test("I receive the matchId");
        gameServer.setMatchId((matchId.isString().stringValue())); 
        gameServer.setPlayerIds(playerIds.isArray());
      }else {
    	//test("I got the state from channel");
    	//test(response.toString());
    	JSONValue state_JSON = response.get("gameState");
    	
    	if(state_JSON!=null) {
    	  JSONObject state = state_JSON.isObject();
    	  //displaying2(state.toString());
    	  logState(gameServer.getMyPlayerId() + ":" + "\n" + state.toString() + "\n");
    	  List<Operation> lastMove_ = Lists.newArrayList();
      	  lastMove_.add(new SetTurn(gameServer.getMyPlayerId()));
      	  JSONArray lastMove = js.serializeMove(lastMove_);
    	  JSONString lastMovePlayerId = new JSONString("1");
    	  //test("To update the board using the state returned by the server from Channel");
    	  gameServer.getGameContainer().updateUi(state, lastMove, lastMovePlayerId, gameServer.getPlayerIds());
    	}
      }
    }else {
      throw new RuntimeException("ERROR!");
    }
  }
  
  public static native void displaying2(String content) /*-{
	$doc.getElementById("state2").innerHTML = content;
  }-*/;
/*
  private static native void test(String message) /*-{
    $wnd.alert(message);
  }-*/;
}
