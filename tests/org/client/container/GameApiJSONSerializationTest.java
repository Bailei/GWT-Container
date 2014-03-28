package org.client.container;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Map;

import org.client.container.Card.Rank;
import org.client.container.Card.Suit;
import org.client.container.GameApi.Delete;
import org.client.container.GameApi.EndGame;
import org.client.container.GameApi.Operation;
import org.client.container.GameApi.Set;
import org.client.container.GameApi.SetTurn;
import org.client.container.GameApi.SetVisibility;
import org.client.container.GameApi.Shuffle;

//import com.google.appengine.labs.repackaged.com.google.common.collect.Maps;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.junit.client.GWTTestCase;

public class GameApiJSONSerializationTest extends GWTTestCase{
    
    JSONSerialization serialTest = new JSONSerialization();
    JSONDeserialization deserialTest = new JSONDeserialization();
    
    private static final String PLAYER_ID = "playerId";
    private static final String W = "W"; // White hand
    private static final String B = "B"; // Black hand
    private static final String M = "M"; // Middle pile
    private static final String C = "C"; // Card key (C0...C51)
    private static final String CLAIM = "claim"; // a claim has the form: [3cards, rankK]
    private static final String IS_CHEATER = "isCheater"; // we claim we have a cheater
    private static final String YES = "yes"; // we claim we have a cheater
    private final int wId = 41;
    private final int bId = 42;
    private final ImmutableList<Integer> visibleToW = ImmutableList.of(wId);
    private final ImmutableList<Integer> visibleToB = ImmutableList.of(bId);
    private final ImmutableMap<String, Object> wInfo =
        ImmutableMap.<String, Object>of(PLAYER_ID, wId);
    private final ImmutableMap<String, Object> bInfo =
        ImmutableMap.<String, Object>of(PLAYER_ID, bId);
    private final ImmutableList<Map<String, Object>> playersInfo =
        ImmutableList.<Map<String, Object>>of(wInfo, bInfo);
    private final ImmutableMap<String, Object> emptyState = ImmutableMap.<String, Object>of();
    private final ImmutableMap<String, Object> nonEmptyState =
        ImmutableMap.<String, Object>of("k", "v");

    private final ImmutableMap<String, Object> emptyMiddle = ImmutableMap.<String, Object>of(
        W, getIndicesInRange(0, 10),
        B, getIndicesInRange(11, 51),
        M, ImmutableList.of());

    // The order of operations: turn, isCheater, W, B, M, claim, C0...C51
    private final ImmutableList<Operation> claimOfW = ImmutableList.<Operation>of(
        new SetTurn(bId),
        new Set(W, getIndicesInRange(0, 8)),
        new Set(M, getIndicesInRange(9, 10)),
        new Set(CLAIM, ImmutableList.of("2cards", "rankA")));

    private final ImmutableList<Operation> claimOfB = ImmutableList.<Operation>of(
        new SetTurn(wId),
        new Set(B, getIndicesInRange(11, 48)),
        new Set(M, getIndicesInRange(49, 51)),
        new Set(CLAIM, ImmutableList.of("3cards", "rankJ")));
    
    private List<Integer> getIndicesInRange(int fromInclusive, int toInclusive) {
        List<Integer> keys = Lists.newArrayList();
        for (int i = fromInclusive; i <= toInclusive; i++) {
          keys.add(i);
        }
        return keys;
      }
    
    List<Operation> getMoveInitial(List<Integer> playerIds) {
        int whitePlayerId = playerIds.get(0);
        int blackPlayerId = playerIds.get(1);
        List<Operation> operations = Lists.newArrayList();
        // The order of operations: turn, isCheater, W, B, M, claim, C0...C51
        operations.add(new SetTurn(whitePlayerId));
        // set W and B hands
        operations.add(new Set(W, getIndicesInRange(0, 25)));
        operations.add(new Set(B, getIndicesInRange(26, 51)));
        // middle pile is empty
        operations.add(new Set(M, ImmutableList.of()));
        // sets all 52 cards: set(C0,2h), �, set(C51,Ac)
        for (int i = 0; i < 52; i++) {
          operations.add(new Set(C + i, cardIdToString(i)));
        }
        // shuffle(C0,...,C51)
        operations.add(new Shuffle(getCardsInRange(0, 51)));
        // sets visibility
        for (int i = 0; i < 26; i++) {
          operations.add(new SetVisibility(C + i, ImmutableList.of(whitePlayerId)));
        }
        for (int i = 26; i < 52; i++) {
          operations.add(new SetVisibility(C + i, ImmutableList.of(blackPlayerId)));
        }
        return operations;
      }
    
    String cardIdToString(int cardId) {
        checkArgument(cardId >= 0 && cardId < 52);
        int rank = (cardId / 4);
        String rankString = Rank.values()[rank].getFirstLetter();
        int suit = cardId % 4;
        String suitString = Suit.values()[suit].getFirstLetterLowerCase();
        return rankString + suitString;
      }
    
    public List<String> getCardsInRange(int fromInclusive, int toInclusive) {
        List<String> keys = Lists.newArrayList();
        for (int i = fromInclusive; i <= toInclusive; i++) {
          keys.add(C + i);
        }
        return keys;
      }

    @Override
    public String getModuleName() {
        // TODO Auto-generated method stub
        return "org.client.Container";
    }
    
    /**
     * Add as many tests as you like.
     */
    public void testOpeation() {                                              // (3)
        JSONArray re = new JSONArray();
        re = serialTest.serializeMove(claimOfW);
        List<Operation> deResult = Lists.newArrayList();
        deResult = deserialTest.deserializeMove(re);
        assertTrue(deResult.containsAll(claimOfW));
        assertTrue(claimOfW.containsAll(deResult));
    }
    
    public void testOpeation1() {                                              // (3)
        JSONArray re = new JSONArray();
        re = serialTest.serializeMove(claimOfB);
        List<Operation> deResult = Lists.newArrayList();
        deResult = deserialTest.deserializeMove(re);
        assertTrue(deResult.containsAll(claimOfB));
        assertTrue(claimOfB.containsAll(deResult));
    }
    
    public void testOpeation2() {                                              // (3)
        JSONArray re = new JSONArray();
        List<Operation> operations = getMoveInitial(ImmutableList.of(wId, bId));
        re = serialTest.serializeMove(operations);
        List<Operation> deResult = Lists.newArrayList();
        deResult = deserialTest.deserializeMove(re);
        assertTrue(deResult.containsAll(operations));
        assertTrue(operations.containsAll(deResult));
    }
    
    public void testOpeation3() {                                              // (3)
        JSONArray re = new JSONArray();
        List<Operation> operations = ImmutableList.<Operation>of(
                new SetTurn(bId),
                new Set(W, getIndicesInRange(0, 8)),
                new Set(M, getIndicesInRange(9, 10)),
                new Set(CLAIM, ImmutableList.of("3cards", "rankA")));
        re = serialTest.serializeMove(operations);
        List<Operation> deResult = Lists.newArrayList();
        deResult = deserialTest.deserializeMove(re);
        assertTrue(deResult.containsAll(operations));
        assertTrue(operations.containsAll(deResult));
    }
    
    public void testOpeation4() {                                              // (3)
        JSONArray re = new JSONArray();
        List<Operation> operations = ImmutableList.<Operation>of(
                new SetTurn(bId),
                new Set(W, getIndicesInRange(0, 7)),
                new Set(M, getIndicesInRange(9, 10)),
                new Set(CLAIM, ImmutableList.of("2cards", "rankA")));
        re = serialTest.serializeMove(operations);
        List<Operation> deResult = Lists.newArrayList();
        deResult = deserialTest.deserializeMove(re);
        assertTrue(deResult.containsAll(operations));
        assertTrue(operations.containsAll(deResult));
    }
    
    public void testOpeation5() {                                              // (3)
        JSONArray re = new JSONArray();
        List<Operation> operations = ImmutableList.<Operation>of(
                new SetTurn(bId),
                new Set(W, getIndicesInRange(0, 8)),
                new Set(M, getIndicesInRange(8, 10)),
                new Set(CLAIM, ImmutableList.of("2cards", "rankA")));
        re = serialTest.serializeMove(operations);
        List<Operation> deResult = Lists.newArrayList();
        deResult = deserialTest.deserializeMove(re);
        assertTrue(deResult.containsAll(operations));
        assertTrue(operations.containsAll(deResult));
    }
    
    public void testOpeation6() {                                              // (3)
        JSONArray re = new JSONArray();
        List<Operation> operations =  ImmutableList.<Operation>of(
                new SetTurn(wId),
                new Set(IS_CHEATER, YES),
                new SetVisibility("C50"), new SetVisibility("C51"));

        re = serialTest.serializeMove(operations);
        List<Operation> deResult = Lists.newArrayList();
        deResult = deserialTest.deserializeMove(re);
        assertTrue(deResult.containsAll(operations));
        assertTrue(operations.containsAll(deResult));
    }
    
    public void testOpeation7() {                                              // (3)
        JSONArray re = new JSONArray();
        List<Operation> operations =  ImmutableList.<Operation>of(
                new SetTurn(bId),
                new Delete(IS_CHEATER),
                new Set(B, getIndicesInRange(11, 51)),
                new Set(M, ImmutableList.of()),
                new Delete(CLAIM),
                new SetVisibility("C50", visibleToB),
                new SetVisibility("C51", visibleToB),
                new Shuffle(getCardsInRange(11, 51)));


        re = serialTest.serializeMove(operations);
        List<Operation> deResult = Lists.newArrayList();
        deResult = deserialTest.deserializeMove(re);
        assertTrue(deResult.containsAll(operations));
        assertTrue(operations.containsAll(deResult));
    }
    
    public void testOpeation8() {                                              // (3)
        JSONArray re = new JSONArray();
        List<Operation> operations =  ImmutableList.<Operation>of(
                new SetTurn(bId),
                new Set(IS_CHEATER, YES),
                new SetVisibility("C0"), new SetVisibility("C1"));

        re = serialTest.serializeMove(operations);
        List<Operation> deResult = Lists.newArrayList();
        deResult = deserialTest.deserializeMove(re);
        assertTrue(deResult.containsAll(operations));
        assertTrue(operations.containsAll(deResult));
    }
    
    public void testOpeation9() {                                              // (3)
        JSONArray re = new JSONArray();
        List<Operation> operations =  ImmutableList.of(
                new SetTurn(wId),
                new Delete(IS_CHEATER),
                new Set(W, getIndicesInRange(0, 51)),
                new Set(M, ImmutableList.of()),
                new Delete(CLAIM),
                new SetVisibility("C50", visibleToW),
                new SetVisibility("C51", visibleToW),
                new Shuffle(getCardsInRange(0, 51)),
                new EndGame(bId));

        re = serialTest.serializeMove(operations);
        List<Operation> deResult = Lists.newArrayList();
        deResult = deserialTest.deserializeMove(re);
        assertTrue(deResult.containsAll(operations));
        assertTrue(operations.containsAll(deResult));
    }
    
    public void testState1(){
        Map<String, Object> state = ImmutableMap.<String, Object>builder()
                .put(IS_CHEATER, YES)
                .put("C50", "Ah")
                .put("C51", "Ah")
                .put(W, getIndicesInRange(0, 10))
                .put(B, getIndicesInRange(11, 49))
                .put(M, getIndicesInRange(50, 51))
                .put(CLAIM, ImmutableList.of("2cards", "rankA"))
                .build();
        
        JSONObject re = new JSONObject();
        re = serialTest.serializeState(state);
        Map<String, Object> deResult = Maps.newHashMap();
        
        
        //System.out.println(re);
        deResult = deserialTest.deserializeState(re);
        System.out.println(deResult);
        assertTrue(deResult.equals(state));
    }
    
    public void testState2(){
        Map<String, Object> state = ImmutableMap.<String, Object>of(
                W, getIndicesInRange(0, 10),
                B, getIndicesInRange(11, 51),
                M, getIndicesInRange(50, 51),
                CLAIM, ImmutableList.of("2cards", "rankA"));
        
        JSONObject re = new JSONObject();
        re = serialTest.serializeState(state);
        Map<String, Object> deResult = Maps.newHashMap();
        
        
        //System.out.println(re);
        deResult = deserialTest.deserializeState(re);
        System.out.println(deResult);
        assertTrue(deResult.equals(state));
    }
    
    public void testState3(){
        Map<String, Object> state = ImmutableMap.<String, Object>builder()
                .put(IS_CHEATER, YES)
                .put("C50", "Ah")
                .put("C51", "Kh")
                .put(W, getIndicesInRange(0, 10))
                .put(B, getIndicesInRange(11, 49))
                .put(M, getIndicesInRange(50, 51))
                .put(CLAIM, ImmutableList.of("2cards", "rankA"))
                .build();
        
        JSONObject re = new JSONObject();
        re = serialTest.serializeState(state);
        Map<String, Object> deResult = Maps.newHashMap();
        
        
        //System.out.println(re);
        deResult = deserialTest.deserializeState(re);
        System.out.println(deResult);
        assertTrue(deResult.equals(state));
    }
    
    public void testState4(){
        Map<String, Object> state = ImmutableMap.<String, Object>builder()
                .put(IS_CHEATER, YES)
                .put("C50", "Ah")
                .put("C51", "As")
                .put(W, getIndicesInRange(0, 49))
                .put(B, ImmutableList.of())
                .put(M, getIndicesInRange(50, 51))
                .put(CLAIM, ImmutableList.of("2cards", "rankA"))
                .build();
        
        JSONObject re = new JSONObject();
        re = serialTest.serializeState(state);
        Map<String, Object> deResult = Maps.newHashMap();
        
        
        //System.out.println(re);
        deResult = deserialTest.deserializeState(re);
        System.out.println(deResult);
        assertTrue(deResult.equals(state));
    }
    
    public void testState5(){
        Map<String, Object> state = ImmutableMap.<String, Object>of(
                W, ImmutableList.of(),
                B, getIndicesInRange(2, 51),
                M, getIndicesInRange(0, 1),
                CLAIM, ImmutableList.of("1cards", "rankA"));
        
        JSONObject re = new JSONObject();
        re = serialTest.serializeState(state);
        Map<String, Object> deResult = Maps.newHashMap();
        
        
        //System.out.println(re);
        deResult = deserialTest.deserializeState(re);
        System.out.println(deResult);
        assertTrue(deResult.equals(state));
    }
    
    public void testState6(){
        Map<String, Object> state = ImmutableMap.<String, Object>builder()
                .put(IS_CHEATER, YES)
                .put("C50", "Ah")
                .put("C51", "As")
                .put(W, getIndicesInRange(0, 49))
                .put(B, ImmutableList.of())
                .put(M, getIndicesInRange(50, 51))
                .put(CLAIM, ImmutableList.of("2cards", "rankA"))
                .build();
        JSONObject re = new JSONObject();
        re = serialTest.serializeState(state);
        Map<String, Object> deResult = Maps.newHashMap();
        
        
        //System.out.println(re);
        deResult = deserialTest.deserializeState(re);
        System.out.println(deResult);
        assertTrue(deResult.equals(state));
    }
    
    public void testState7(){
        Map<String, Object> state = ImmutableMap.<String, Object>of();
        JSONObject re = new JSONObject();
        re = serialTest.serializeState(state);
        Map<String, Object> deResult = Maps.newHashMap();
        
        
        //System.out.println(re);
        deResult = deserialTest.deserializeState(re);
        System.out.println(deResult);
        assertTrue(deResult.equals(state));
    }
//    public void testFalse() {                                              // (3)
//        assertTrue(false);
//      }

}
