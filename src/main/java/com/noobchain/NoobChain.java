package com.noobchain;
import java.util.ArrayList;
import com.google.gson.GsonBuilder;
import java.security.Security;
import java.util.Base64;
import java.util.HashMap;


public class NoobChain {
    public static ArrayList<Block> blockchain=new ArrayList<Block>();
    public static HashMap<String,TransactionOutput> UTXOs=new HashMap<String, TransactionOutput>();
    public static int difficulty=3;
    public static float minimumTransaction=0.1f;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;


    public static void main(String[] args){
//        blockchain.add(new Block("1st block", "0"));
//        System.out.println("Trying to mine Block 1...");
//        blockchain.get(0).mineBlock(difficulty);
//
//        blockchain.add(new Block("2nd block", blockchain.get(blockchain.size()-1).hash));
//        System.out.println("Trying to mine Block 2...");
//        blockchain.get(1).mineBlock(difficulty);
//
//        blockchain.add(new Block("3rd block", blockchain.get(blockchain.size()-1).hash));
//        System.out.println("Trying to mine Block 3...");
//        blockchain.get(2).mineBlock(difficulty);
//
//        System.out.println("\n Blockchain is Valid: "+ isChainValid());
//
//        String blockchainJson= new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
//        System.out.println("\n The Block Chain: ");
//        System.out.println(blockchainJson);

        //setup Bouncey castle as a security provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        //create new wallets
        walletA=new Wallet();
        walletB=new Wallet();
        Wallet coinbase=new Wallet();

        //create genesis transaction, which sends 100 NoobCoin to walletA
        genesisTransaction=new Transaction(coinbase.publicKey, walletA.publicKey, 100f,null);
        genesisTransaction.generateSignature(coinbase.privateKey);// manually sign the genesis transaction
        genesisTransaction.transactionId="0"; //manually set the transaction id
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); // manually add the transaction output
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list

        System.out.println("Creating and Mining Genesis Block...");
        Block genesis=new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        //testing
        Block block1=new Block(genesis.hash);
        System.out.println("\n WalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletA is attempting to send funds (40) to walletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey,40f));
        addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletB's balance is: " + walletB.getBalance());

        Block block2=new Block(block1.hash);
        System.out.println("\nWalletA attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey,1000f));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletB's balance is: " + walletB.getBalance());

        Block block3=new Block(block2.hash);
        System.out.println("\nWalletB is attempting to send funds (20) to walletA...");
        block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletB's balance is: " + walletB.getBalance());

        isChainValid();


    }
    public static boolean isChainValid(){
        Block currentBlock;
        Block previousBlock;
        String hashTarget=new String(new char[difficulty]).replace('\0','0');
        HashMap<String,TransactionOutput> tempUTXOs=new HashMap<String, TransactionOutput>();
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));


        for(int i=1; i<blockchain.size();i++){
            currentBlock=blockchain.get(i);
            previousBlock=blockchain.get(i-1);

            //compare registered hash and calculated hash
            if(!currentBlock.hash.equals(currentBlock.calculateHash())){
                System.out.println("#Current Hash not equal");
                return false;
            }

            if(!previousBlock.hash.equals(currentBlock.previousHash)){
                System.out.println("#Previous Hash not equal");
                return false;
            }

            //check if hash is solved
            if(!currentBlock.hash.substring(0,difficulty).equals(hashTarget)){
                System.out.println("This block hasn't been mined");
                return false;
            }

            //loop thru blockchain transactions:
            TransactionOutput tempOutput;
            for(int t=0;t<currentBlock.transactions.size();t++){
                Transaction currentTransaction=currentBlock.transactions.get(t);

                if(!currentTransaction.verifySignature()){
                    System.out.println("Signature on transaction("+t+") is valid");
                    return false;
                }
                if(currentTransaction.getInputsValue()!=currentTransaction.getOutputsValue()){
                    System.out.println("#Inputs are not equal to outputs on Transaction("+t+")");
                    return false;
                }
                for(TransactionInput input: currentTransaction.inputs){
                    tempOutput=tempUTXOs.get(input.transactionOutputId);

                    if(tempOutput==null){
                        System.out.println("#Referenced input on transaction("+t+") is Missing");
                        return false;
                    }
                    if(input.UTXO.value !=tempOutput.value){
                        System.out.println("#Referenced input Transaction("+t+") value is Invalid");
                        return false;
                    }
                    tempUTXOs.remove(input.transactionOutputId);
                }
                for (TransactionOutput output: currentTransaction.outputs){
                    tempUTXOs.put(output.id, output);
                }
                if(currentTransaction.outputs.get(0).reciepient!= currentTransaction.reciepient){
                    System.out.println("#Transaction ("+t+") output recipient is not who it should be");
                    return false;
                }
                if(currentTransaction.outputs.get(1).reciepient != currentTransaction.sender){
                    System.out.println("#Transaction ("+t+") output 'change' is not sender.");
                    return false;
                }
            }
        }
        System.out.println("Blockchain is valid");
        return true;
    }
    public static void addBlock(Block newBlock){
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}
