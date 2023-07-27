/*
Author Name: Tanvi Murke
Andrew EmailID: tmurke@andrew.cmu.edu
 */
package org.example;

import com.google.gson.Gson;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Blockchain {
    //declare variables
    public List<Block> blockchain;
    public String chainHash;
    public int hashesPerSecond;

    //constructor
    public Blockchain() {
       blockchain = new ArrayList<>();
       chainHash = "";
       hashesPerSecond = 0;
    }

    //A new Block is being added to the BlockChain
    public void addBlock(Block newBlock){
            //set current hash as previous hash
            newBlock.setPreviousHash(chainHash);
            //add block in chain(list)
            blockchain.add(newBlock);
            //get hash by proof of work
            chainHash = newBlock.proofOfWork();
    }


    //This method computes exactly 2 million hashes and times how long that process takes.
    public void computeHashesPerSecond(){
        String hashstring = "00000000";
        int hashes = 0;
        Timestamp starttime = getTime();
        //computes 2 million
        while(hashes < 2000000){
            //calculate hash
            calculateHash(hashstring);
            hashes++;
        }
        Timestamp endtime = getTime();
        //check time taken for hashing
        long noOfSeconds  = endtime.getTime() - starttime.getTime();
        //calculate hashes per second
        hashesPerSecond = (int) (hashes / noOfSeconds);
    }

    //same method that is used in Block to calculate the Hash
    public String calculateHash(String input) {
    String hash = "";
        try {
            String hashmethod = "SHA-256";
            //implementation of hashing
            MessageDigest md = MessageDigest.getInstance(hashmethod);
            //update messagedigest
            md.update(input.getBytes());
            //stores in byte array
            byte[] hashvalue = md.digest();
            //used to print hash value
            hash = bytesToHex(hashvalue);

        } catch (NoSuchAlgorithmException e) {
            //if hashing is not available
            System.out.println("No hashing available" + e);
            e.printStackTrace();
        }
        return hash;
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    //used this code from LAB 1
    public static String bytesToHex(byte[] bytes) {

        char[] hexChars = new char[bytes.length * 2];

        for (int j = 0; j < bytes.length; ++j) {
            int v = bytes[j] & 255;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 15];
        }

        return new String(hexChars);
    }

    //return block at position i
    public Block getBlock(int i){
        return blockchain.get(i);
    }

    //simple getter for chain hash
    public String getChainHash() {
        return chainHash;
    }

    //simple setter for chain hash
    public void setChainHash(String chainHash) {
        this.chainHash = chainHash;
    }

    //returns the size of the chain in blocks
    public int getChainSize(){
        return blockchain.size();
    }

    //get hashes per second
    public int getHashesPerSecond() {
        return hashesPerSecond;
    }
    //setter for hashes per second
    public void setHashesPerSecond(int hashesPerSecond) {
        this.hashesPerSecond = hashesPerSecond;
    }

    //a reference to the most recently added Block
    public Block getLatestBlock(){
        return blockchain.get(blockchain.size()-1);
    }

    //returns the current system time
    public Timestamp getTime(){
        return new Timestamp(System.currentTimeMillis());
    }

    //compute and return the total difficulty of all blocks on the chain. Each block knows its own difficulty.
    public int getTotalDifficulty(){
        int total = 0;
        for (Block b: blockchain) {
            total += b.getDifficulty();
        }
        return total;
    }

    //Compute and return the expected number of hashes required for the entire chain
    public double getTotalExpectedHashes(){
        double total = 0;
        for(Block b: blockchain) {
            total += Math.pow(16, b.getDifficulty());
        }
        return total;
    }

    //If the chain only contains one block, the genesis block at position 0, this routine computes the hash of the block
    // and checks that the hash has the requisite number of leftmost 0's (proof of work) as specified in the difficulty field.
    public String isChainValid(){
        String genhash = "";
        //first block is genesis block
        Block genesis = blockchain.get(0);
        //get genesis block hash
        genhash = genesis.calculateHash();

        int chainSize = blockchain.size();
        //genesis block leading zero
        String leadingzerogen = "0".repeat(genesis.getDifficulty());

        //if chain has only genesis block
        if(chainSize == 1){
            //check for hash and leading zero condition
            if((!chainHash.equals(genhash)) || (!genhash.startsWith(leadingzerogen)) ){
                return "Invalid Genesis";
            }
            return "TRUE";
        }else{
            //chain has more than 2 blocks
            Block current, previous;
            String currenthash = "";
            for(int i=1; i<blockchain.size();i++){
                //get current and previous block
                current = blockchain.get(i);
                previous = blockchain.get(i-1);
                //check the previous hash condition
                if(!current.getPreviousHash().equals(previous.calculateHash())){
                    return "FALSE";
                }
                //calculate current hash
                currenthash = current.calculateHash();
                if (!currenthash.startsWith(leadingzerogen)) {
                    return "FALSE \nImproper hash on node "+i+ " Does not begin with "+leadingzerogen ;
                }
            }
            Block last = blockchain.get(blockchain.size() - 1);
            //check hash for last block
            if (!chainHash.equals(last.calculateHash())) {
                return "FALSE";
            }
            return "TRUE";
        }

    }

    //This routine repairs the chain.
    public void repairChain(){
        for(int i = 0; i < getChainSize() - 1; i++) {
            //get current and next block
            Block current = getBlock(i);
            Block next = getBlock(i + 1);
            //current hash and next block's previous hash are same
            next.previousHash = current.proofOfWork();
        }
        //recalculate hash using proof of work
        chainHash = getBlock(getChainSize() - 1).proofOfWork();
    }

    @Override
    public String toString() {
        //add all blocks
        Blockchain bc = new Blockchain();
        for(int i = 0; i < getChainSize(); i++) {
            bc.blockchain.add(getBlock(i));
        }
        bc.hashesPerSecond = getHashesPerSecond();
        bc.chainHash = getChainHash();
        //used this code from prereq of Project 3
        Gson gson = new Gson();
        String messageToSend = gson.toJson(bc);
        return messageToSend;
    }

    public static void main(String[] args) {
        //create genesis block
        Blockchain bc = new Blockchain();
        Block b = new Block(bc.getChainSize(), bc.getTime(), "Genesis", 2);
        bc.addBlock(b);
        bc.computeHashesPerSecond();

        while(true){
            //menu
            System.out.println("0. View basic blockchain status.\n" +
                    "1. Add a transaction to the blockchain.\n" +
                    "2. Verify the blockchain.\n" +
                    "3. View the blockchain.\n" +
                    "4. Corrupt the chain.\n" +
                    "5. Hide the Corruption by repairing the chain.\n" +
                    "6. Exit");
            Scanner sc = new Scanner(System.in);
            int choice = sc.nextInt();
            //after user choice
            switch(choice){
                case 0:
                    //to view basic status of blockchain
                    System.out.println("Current size of chain: " + bc.getChainSize());
                    System.out.println("Difficulty of most recent block: " + bc.getLatestBlock().getDifficulty());
                    System.out.println("Total Difficulty for all blocks: " + bc.getTotalDifficulty());
                    System.out.println("Approximate hashes per second on this machine: " + bc.getHashesPerSecond());
                    System.out.println("Expected total hashes required for the whole chain: " + bc.getTotalExpectedHashes());
                    System.out.println("Nonce for most recent block: " + bc.getLatestBlock().getNonce());
                    System.out.println("Chain hash: " + bc.getChainHash());
                    break;
                case 1:
                    //add block
                    System.out.println("Enter difficulty > 0");
                    Scanner sc1 = new Scanner(System.in);
                    int difficulty = sc1.nextInt();
                    Scanner sc2 = new Scanner(System.in);
                    System.out.println("Enter transaction");
                    String data = sc2.nextLine();
                    Block newBlock = new Block(bc.getChainSize(), bc.getTime(), data, difficulty);
                    long startTime = System.currentTimeMillis();
                    bc.addBlock(newBlock);
                    long endTime = System.currentTimeMillis();
                    System.out.println("Total execution time to add this block was " + (endTime - startTime) + " milliseconds");
                    break;
                case 2:
                    //chain verification
                    startTime = System.currentTimeMillis();
                    System.out.println("Chain verification: " + bc.isChainValid());
                    endTime = System.currentTimeMillis();
                    System.out.println("Total execution time required to verify the chain was " + (endTime - startTime) + " milliseconds");
                    break;
                case 3:
                    //display blockchain
                    System.out.println(bc.toString());
                    break;
                case 4:
                    //corrupt the blockchain
                    System.out.println("corrupt the Blockchain");
                    System.out.print("Enter block ID of block to corrupt \n");
                    Scanner sc3 = new Scanner(System.in);
                    int id = sc3.nextInt();
                    System.out.print("Enter new data for block " + id + ":\n");
                    sc.nextLine();
                    String corrupt = sc.nextLine();
                    Block corruptBlock = bc.blockchain.get(id);
                    corruptBlock.setData(corrupt);
                    System.out.printf("Block " +id+ " now holds "+corrupt +"\n");
                    break;
                case 5:
                    //repair the corrupted blockchain
                    startTime = System.currentTimeMillis();
                    bc.repairChain();
                    endTime = System.currentTimeMillis();
                    System.out.println("Total execution time required to repair the chain was " + (endTime - startTime) + " milliseconds");
                    break;
                case 6:
                    //exit
                    System.exit(0);
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

}
/*
Data Analysis
Enter difficulty > 0
4
Enter transaction
a pays b 10 coins
Total execution time to add this block was 178 milliseconds

Enter difficulty > 0
6
Enter transaction
d pays q 50 coins
Total execution time to add this block was 388 milliseconds

For addBlock:
Here, we can say that after increasing the difficulty the time required for adding the block also increases

For isValid:
Chain verification: TRUE
Total execution time required to verify the chain was 2 milliseconds
Validation time has also increased

Enter new data for block 1:
a pays g 15 coins
Block 1 now holds a pays g 15 coins

For chainRepair:
Total execution time required to repair the chain was 25836 milliseconds
We can see here that after increasing the difficulty and corrupting the block the time to repair is tremendously large

Thus we can conclude that as difficulty increases the time required for all the operations also increases.
 */