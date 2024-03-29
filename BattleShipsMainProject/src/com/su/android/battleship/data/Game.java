package com.su.android.battleship.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Class that holds the business logic of the battleship game
 * @author vasko
 *
 */
public class Game implements Serializable {

	private static final long serialVersionUID = 6204938308440070031L;

	/**
	 * hard-coded game board board side
	 */
	public static final short BOARD_SIDE = 10;
	
	/**
	 * hard-coded game board size
	 */
	public static final short BOARD_SIZE = 100;
	
	/**
	 * hard-coded ships count
	 */
	public static final short SHIPS_COUNT = 5;
	
	/**
	 * hard-coded ships sizes
	 */
	public static final short[] SHIPS_SIZES = new short[]{5,4,3,2,2};
		
	
	/*Can take values 0 and 1 - stands for the index of the player which is to make move*/
	@SuppressWarnings("unused") //will be used in the future
	private short playerOnMove;
	
	/*Has two elements - the boardArray of player0 and the boardArray of player_1*/
	private List<Short[]> playersBoards;
	
	/*Has two elements - the shipArray of player0 and the shipArray of player_1*/
	private List<Ship[]> playersShips;
	
	private boolean gameOver = false;
	
	/*this list of two elements hold the number of players' destroyed ships
	 *If one of the two numbers becomes equal to the ships count - the game is over
	 */
	private List<Short> destroyedShipsCount;
	
	/**
	 * 
	 * @param playerOnMove - the player to make the first move
	 * @param firstPlayerShips
	 * @param secondPlayerShips
	 */
	public Game(short playerOnMove,Ship[] firstPlayerShips,Ship[] secondPlayerShips) {
		this.playerOnMove = playerOnMove;
		
		createStartingBoards();//create and init the two game boards
		
		playersShips = new ArrayList<Ship[]>(2);
		playersShips.add(firstPlayerShips);
		playersShips.add(secondPlayerShips);
		
		updateStartingBoardsWithPlayersShips(firstPlayerShips,secondPlayerShips);
		
		destroyedShipsCount = new ArrayList<Short>(2);
		destroyedShipsCount.add((short)0);
		destroyedShipsCount.add((short)0);
	}
	
	/**
	 * @param playerIndex - index of desired player - 0 or 1
	 * @return - desired player's board representation as a short[] 
	 */
	public Short[] getPlayerBoard(short playerIndex){
		return playersBoards.get(playerIndex);
	}
	
	
	/**
	 * 
	 * @param playerIndex - index of desired player - 0 or 1
	 * @return player's ships representation as a Ship[]
	 */
	public Ship[] getPlayerShips(short playerIndex){
		return playersShips.get(playerIndex);
	}
	
	
	/**
	 * 
	 * @param playerIndex - index of desired player - 0 or 1
	 * @param ships - player's ships representation
	 */
	public void setPlayerShips(short playerIndex,Ship[] ships){
		playersShips.set(playerIndex, ships);
	}
	
	
	
	/**
	 * Updates the game state after player makes a move
	 * @param attackingPlayer - should always be 0 or 1 - index of the player who makes the move
	 * @param fieldPosition - the position of the selected field
	 * @return - the new status of the targeted field
	 */
	public final short executeMove(short attackingPlayer,short fieldPosition){
		short attackedPlayer = getOppositePlayer(attackingPlayer);
		Short[] board = playersBoards.get(attackedPlayer);
		short prevFieldStatus = board[fieldPosition];		
		short newFieldStatus = BoardFieldStatus.getAttackedFieldCode(prevFieldStatus);
		board[fieldPosition] = newFieldStatus;

		
		if(BoardFieldStatus.isShipNotAttackedStatus(prevFieldStatus)){
			short shipIndex = BoardFieldStatus.getShipIndex(prevFieldStatus);
			Ship tempShip = playersShips.get(attackedPlayer)[shipIndex];
			boolean isHit = tempShip.updateShipState(fieldPosition);
			if(isHit){//it is always a hit!
				if(tempShip.isShipDestroyed()){
					newFieldStatus = updateDestroyedShipBoardState(attackedPlayer,tempShip,shipIndex);					 
					short oldCount = destroyedShipsCount.get(attackedPlayer);
					destroyedShipsCount.set(attackedPlayer,(short) (oldCount+1));					
					if(destroyedShipsCount.get(attackedPlayer) == SHIPS_COUNT){
						gameOver = true;
					}
				}
			}else{
				//TODO : throw exception - ship is always hit , because of the boardFieldStatus check, that shows the field is a notHitShipField
			}
		}
		return newFieldStatus;
	}
	
	private short updateDestroyedShipBoardState(short player,Ship ship,short shipIndex){
		short destroyedShipFieldStatus = BoardFieldStatus.getDestroyedShipCode(shipIndex);
		for(short s : ship.getBoardFields() ){
			this.playersBoards.get(player)[s] = destroyedShipFieldStatus;
		}
		return destroyedShipFieldStatus;
	}
	
	/**
	 * 
	 * @return whether game is over or not
	 */
	public boolean isGameOver(){
		return gameOver;		
	}
	
	private void createStartingBoards(){
		Short[] board_0 = new Short[BOARD_SIZE];
		Short[] board_1 = new Short[BOARD_SIZE];
		
		for(int i = 0 ; i < BOARD_SIZE ; i++ ){
			board_0[i] = BoardFieldStatus.WATER;
			board_1[i] = BoardFieldStatus.WATER;
		}
		
		this.playersBoards = new ArrayList<Short[]>(2);
		this.playersBoards.add(board_0);
		this.playersBoards.add(board_1);
	}
	
	private void updateStartingBoardsWithPlayersShips(Ship[] firstPlayerShips,Ship[] secondPlayerShips){
		for(int i = 0 ; i < SHIPS_COUNT ; i++ ){
			updateBoardWithShipPosition(0,i,firstPlayerShips[i]);
			updateBoardWithShipPosition(1,i,secondPlayerShips[i]);
		}
	}
	
	private void updateBoardWithShipPosition(int boardIndex,int shipIndex,Ship ship){
		Short[] board = this.playersBoards.get(boardIndex);
		short[] shipFieldsAsBoardIndexes = ship.getBoardFields();
		short boardMarker = BoardFieldStatus.getShipMarkerCode((short)shipIndex);
		short index;
		for(int i = 0 ; i < ship.getLength() ; i++){
			index = shipFieldsAsBoardIndexes[i];
			board[index] = boardMarker;
		}
	}
	
	private short getOppositePlayer(short index){
		short result = -1;
		if(index == 0){
			result = 1;
		}
		if(index == 1){
			result = 0;
		}
		//TODO : handle IABE exception - index should be 0 or 1
		return result;
	}
	
	/**
	 * 
	 * @return Map<ShipLength,Count>
	 */
	public Map<Short, Short> getShipsMapRepresentation(){
		Map<Short, Short> resultMap = new HashMap<Short, Short>();
		
		Ship[] ships = this.playersShips.get(0);
		short tempLength;
		for(int i = 0 ; i < ships.length ; i++){
			tempLength = ships[i].getLength();
			if(resultMap.containsKey(tempLength)){
				short currentCount = resultMap.get(tempLength);
				resultMap.put(tempLength, (short) (currentCount+1));
			}else{
				resultMap.put(tempLength, (short) 1);
			}
		}
		
		return resultMap;
	}
}
