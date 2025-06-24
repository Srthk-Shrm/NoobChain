import java.util.ArrayList;
import com.google.gson.GsonBuilder;
import java.security.Security;
import java.util.Base64;


public class NoobChain {
    public static ArrayList<Block> blockchain=new ArrayList<Block>();
    public static int difficulty=5;
    public static Wallet walletA;
    public static Wallet walletB;


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
        //test public and private keys
        System.out.println("Public and private keys:");
        System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
        System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
        //create a test transaction from walletA to walletB
        Transaction transaction=new Transaction(walletA.publicKey, walletB.publicKey, 5,null);
        transaction.generateSignature(walletA.privateKey);
        //verify the signature works and verify it from the public key
        System.out.println("is Signature Verified");
        System.out.println(transaction.verifySignature());


    }
    public static boolean isChainValid(){
        Block currentBlock;
        Block previousBlock;
        String hashTarget=new String(new char[difficulty]).replace('\0','0');

        for(int i=1; i<blockchain.size();i++){
            currentBlock=blockchain.get(i);
            previousBlock=blockchain.get(i-1);

            //compare registered hash and calculated hash
            if(!currentBlock.hash.equals(currentBlock.calculateHash())){
                System.out.println("Current Hash not equal");
                return false;
            }

            if(!previousBlock.hash.equals(currentBlock.previousHash)){
                System.out.println("Previous Hash not equal");
                return false;
            }

            //check if hash is solved
            if(!currentBlock.hash.substring(0,difficulty).equals(hashTarget)){
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }
}
