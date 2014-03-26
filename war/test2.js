 //test for get friend list
 function testGetFriendList() {
	  var xmlhttp = helper.getObjectXHR();
	  var url = "2.smg-server.appspot.com/players/..?" + "accessSignature=YSHEODJSGDK";
	  xmlhttp.onreadystatechange = function() {
	      var resContent = JSON.parse(xmlhttp.responseText);
	      helper.assert(resContent.hasOwnProperty("status"));
	      if(helper.equals(resContent.status, "GET_SUCCESS")) {
	        helper.assert(resContent.hasOwnProperty("friendList"));
	        var friendList = resContent.friendList;
	        for(var i = 0;i<friendList.length;i++) {
	          helper.assert(friendList[i].hasOwnProperty("name"));
	          helper.assert(friendList[i].hasOwnProperty("email"));
	          helper.assert(friendList[i].hasOwnProperty("rate"));
	        }
	     }
	  }
	  xmlhttp.open("GET", "url", true);
	  xmlhttp.send(null);
	}

	//test for add a friend
	function testAddFriend() {
	  var xmlhttp = helper.getObjectXHR();
	  var url = "2.smg-server.appspot.com/players/..";
	  var postInfo = {
	    "accessSignature": "YSHEODJSGDK",
	    "gameId": "5",
	    "matchId": "20",
	    "playerId": "42",
	    "userName": "andy"
	  }
	  xmlhttp.onreadystatechange = function() {
	    var resContent = JSON.parse(xmlhttp.responseText);
	    helper.assert(resContent.hasOwnProperty("status"));
	    if(helper.equals(resContent.status, "SUCCESS_ADD")) {
	        helper.assert(resContent.hasOwnProprty("userName"));
	    }
	  }
	  xmlhttp.open("POST", url, true);
	  xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	  xmlhttp.send(postInfo);
	}

	//test for delete a friend
	function testDeleteSomeFriend() {
	  var xmlhttp = helper.getObjectXHR();
	  var url = "2.appname.appspot.com/players/..?accessSignature= \
	  YSHEODJSGDK&userName=andy";
	  xmlhttp.onreadystatechange = function() {
	    var resContent = JSON.parse(xmlhttp.responseText);
	    helper.assert(resContent.hasOwnProperty("status"));
	    if(helper.equals(resContent.status, "SUCCESS_DELETE")) {
	        helper.assert(resContent.hasOwnProperty("userName"));
	    }
	  }
	  xmlhttp.open("DELETE", url, true);
	  xmlhttp.send(null);
	}

	//test for invite
	function testInviteFriend() {
	  var xmlhttp = helper.getObjectXHR();
	  var url = "2.smg-server.appspot.com/matches/..";
	  var postInfo = {
	    "accessSignature":"YSHEODJSGDK",
	    "gameId":"5",
	    "userName":"andy"
	  }
	  xmlhttp.onreadystatechange = function() {
	    var resContent = JSON.parse(xmlhttp.responseText);
	    helper.assert(resContent.hasOwnProperty("status"));
	    if(helper.equals(resContent.status,"SUCCESS_INVITE")) {
	        helper.assert(resContent.hasOwnProperty("matchId"));
	        helper.assert(resContent.hasOwnProperty("playerId"));
	        helper.assert(resContent.hasOwnProperty("userName"));
	    }
	  }
	  xmlhttp.open("POST", url, true);
	  xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	  xmlhttp.send(postInfo);
	}

	function getMyTokensTest() {
	      var xmlhttp = helper.getObjectXHR();
	      var url = "2.smg-server.appspot.com/players/..";
	      if(!xmlhttp)
	        alert("Error initializing XMLHttpRequest!");
	      else{
	        var postInfo = {
	        "request":"GetMyTokens",
	        "accessSignature":"YSHEODJSGDK",
	        "playerId":"41",
	        };
	        xmlhttp.open("POST", url, true);
	        xmlhttp.onreadystatechange = callbackForGetMyTokensTest;
	        xmlhttp.send(postInfo);
	      }
	    }

	     function callbackForGetMyTokensTest(){
	      if(xmlhttp.readystate == 4){
	        if(xmlhttp.status == 200){
	         var resContent = JSON.parse(xmlhttp.responseText);
	         helper.assert(resContent.hasOwnProperty("status"));
	         helper.assert(resContent.hasOwnProperty("playerId"));
	         helper.assert(resContent.hasOwnProperty("tokensLeft"));
	        }else if(xmlhttp.status == 404){
	           alert("Requset URL dose not exist!");  
	        }else{
	           alert("Error: status is " + xmlhttp.status);
	        }
	      }
	    } 

	     //I want to get my ranking among all players from the server.
	     function getMyRankTest() {
	      var xmlhttp = helper.getObjectXHR();
	      var url = "2.smg-server.appspot.com/players/..";
	      if(!xmlhttp)
	        alert("Error initializing XMLHttpRequest!");
	      else{
	        var postInfo = {
	        "request":"GetMyRank",
	        "accessSignature":"YSHEODJSGDK",
	        "playerId":"41"
	        };
	        xmlhttp.open("POST", URL, true);
	        xmlhttp.onreadystatechange = callbackForGetMyRankTest;
	        xmlhttp.send(postInfo);
	      }
	    }

	     function callbackForGetMyRankTest(){
	      if(xmlhttp.readystate == 4){
	        if(xmlhttp.status == 200){
	         var resContent = JSON.parse(xmlhttp.responseText);
	         helper.assert(resContent.hasOwnProperty("status"));
	         helper.assert(resContent.hasOwnProperty("playerId"));
	         helper.assert(resContent.hasOwnProperty("rankInAllPlayers"));
	        }else if(xmlhttp.status == 404){
	        	alert("Requset URL dose not exist!");  
	        }else{
	        	alert("Error: status is " + xmlhttp.status);
	        }
	      }
	    } 

	    //I want to send the token number of the player betting on the current match.
	    function sendBetInfoTest() {
	      var xmlhttp = helper.getObjectXHR();
	      var url = "2.smg-server.appspot.com/players/..";
	      if(!xmlhttp)
	        alert("Error initializing XMLHttpRequest!");
	      else{
	        var postInfo = {
	        "request": "Bet",	
	        "accessSignature":"YSHEODJSGDK",
	        "gameId":"5",
	        "matchId":"10",
	        "playerId":"41",
	        "tokensBet":"2",
	        };
	        xmlhttp.open("POST", url, true);
	        xmlhttp.onreadystatechange = callbackForSendBetInfoTest;
	        xmlhttp.send(postInfo);
	      }
	    }

	    function callbackForSendBetInfoTest(){
	      if(xmlhttp.readystate == 4){
	        if(xmlhttp.status == 200){
	         var resContent = JSON.parse(xmlhttp.responseText);
	         helper.assert(resContent.hasOwnProperty("status"));
	         helper.assert(resContent.hasOwnProperty("playerId"));
	         helper.assert(resContent.hasOwnProperty("tokensLeft"));
	        }else if(xmlhttp.status == 404){
	        	alert("Requset URL dose not exist!");  
	        }else{
	        	alert("Error: status is " + xmlhttp.status);
	        }
	      }
	    }
	    
	 // test for get my opponent's tokens
	    function getMyOpponentTokensTest() {
	      var xmlhttp = helper.getObjectXHR(); 
	      if(!xmlhttp)
	        alert("Error initializing XMLHttpRequest!");
	      else{
	        var postInfo = {
	        "request":"GetMyOpponentTokens",
	        "accessSignature":"YSHEODJSGDK",
	        "playerId":"50",
	        };
	        xmlhttp.open("POST", "2.smg-server.appspot.com", true);
	        xmlhttp.onreadystatechange = callbackForGetMyOpponentTokensTest;
	        xmlhttp.send(postInfo);
	      }
	    }

	     function callbackForGetMyOpponentTokensTest(){
	      if(xmlhttp.readystate == 4){
	        if(xmlhttp.status == 200){
	         var re = JSON.parse(xmlhttp.responseText);
	         helper.assert(re.hasOwnProperty("status"));
	         helper.assert(re.hasOwnProperty("playerId"));
	         helper.assert(re.hasOwnProperty("tokensLeft"));
	        }else if(xmlhttp.status == 404){
	          urlNotExist();
	        }else{
	          alertStatus();
	        }
	      }
	    } 
	     
	   //test for get opponent rank
	     function getMyOpponentRankTest() {
	      var xmlhttp = helper.getObjectXHR(); 
	      if(!xmlhttp)
	        alert("Error initializing XMLHttpRequest!");
	      else{
	        var postInfo = {
	        "request":"GetMyOpponentRank",
	        "accessSignature":"YSHEODJSGDK",
	        "playerId":"50",
	        };
	        xmlhttp.open("POST", "2.smg-server.appspot.com", true);
	        xmlhttp.onreadystatechange = callbackForGetMyOpponentRankTest;
	        xmlhttp.send(postInfo);
	      }
	    }

	     function callbackForGetMyOpponentRankTest(){
	      if(xmlhttp.readystate == 4){
	        if(xmlhttp.status == 200){
	         var re = JSON.parse(xmlhttp.responseText);
	         helper.assert(re.hasOwnProperty("status"));
	         helper.assert(re.hasOwnProperty("playerId"));
	         helper.assert(re.hasOwnProperty("rankInAllPlayers"));
	        }else if(xmlhttp.status == 404){
	          urlNotExist();
	        }else{
	          alertStatus();
	        }
	      }
	    } 
	     
	 //  test for give up betting
	     function sendStopBetInfoTest() {
	      var xmlhttp = helper.getObjectXHR(); 
	      if(!xmlhttp)
	        alert("Error initializing XMLHttpRequest!");
	      else{
	        var postInfo = {
	        "request": "StopBet",	
	        "accessSignature":"YSHEODJSGDK",
	        "gameId":"5",
	        "matchId":"10",
	        };
	        xmlhttp.open("POST", "2.smg-server.appspot.com", true);
	        xmlhttp.onreadystatechange = callbackForSendStopBetInfoTest;
	        xmlhttp.send(postInfo);
	      }
	    }

	    function callbackForSendStopBetInfoTest(){
	      if(xmlhttp.readystate == 4){
	        if(xmlhttp.status == 200){
	         var re = JSON.parse(xmlhttp.responseText);
	         helper.assert(re.hasOwnProperty("status"));
	         helper.assert(re.hasOwnProperty("tokensLeft"));
	        }else if(xmlhttp.status == 404){
	          urlNotExist();
	        }else{
	          alertStatus();
	        }
	      }
	    }
	    
	   //test for add up betting
	     function sendContinueBetInfoTest() {
	      var xmlhttp = helper.getObjectXHR(); 
	      if(!xmlhttp)
	        alert("Error initializing XMLHttpRequest!");
	      else{
	        var postInfo = {
	        "request": "ContinueBet",	
	        "accessSignature":"YSHEODJSGDK",
	        "gameId":"5",
	        "matchId":"10",
	        };
	        xmlhttp.open("POST", "2.smg-server.appspot.com", true);
	        xmlhttp.onreadystatechange = callbackForSendContinueBetInfoTest;
	        xmlhttp.send(postInfo);
	      }
	    }

	    function callbackForSendContinueBetInfoTest(){
	      if(xmlhttp.readystate == 4){
	        if(xmlhttp.status == 200){
	         var re = JSON.parse(xmlhttp.responseText);
	         helper.assert(re.hasOwnProperty("status"));
	         helper.assert(re.hasOwnProperty("tokensHaveBet"));
	        }else if(xmlhttp.status == 404){
	          urlNotExist();
	        }else{
	          alertStatus();
	        }
	      }
	    }


