package com.noobchain;
import java.util.ArrayList;
import java.util.Date;
public class Block {

    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions=new ArrayList<Transaction>(); //our data will be a simple message
    private long timeStamp; //as number of milliseconds since 1/1/1970.
    private int nonce;

    //Block Constructor

    public Block(String previousHash){

        this.previousHash=previousHash;
        this.timeStamp=new Date().getTime();

        this.hash=calculateHash(); //maming sure we do this after we set the other values
    }
    //calculate new hash based on blocks contents
    public String calculateHash(){
        String calculatedHash=StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp)+
                        Integer.toString(nonce)+
                        merkleRoot);
        return calculatedHash;
    }

    //increase nonce value until hash target is reached
    public void mineBlock(int difficulty){
        merkleRoot=StringUtil.getMerkleRoot(transactions);
        String target=StringUtil.getDifficultyString(difficulty); //Create a string with difficulty * "0"
        while(!hash.substring(0,difficulty).equals(target)){
            nonce++;
            hash=calculateHash();
        }
        System.out.println("Block Mined !! : "+ hash);
    }

    //add transactions to this block
    public boolean addTransaction(Transaction transaction){
        //process transaction and check if valid, unless black is genesis block then ignore
        if(transaction==null) return false;
        if((previousHash != "0")){
            if((transaction.processTransaction()!= true)){
                System.out.println("Transaction failed to process. Discarded");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully Added to Block");
        return true;
    }
}
