import java.security.*;
import java.util.ArrayList;

public class Transaction {

    public String transactionId; // this is also the hsah of the transaction
    public PublicKey sender; // senders address/public key
    public PublicKey reciepient;// receiver's address/public key
    public float value;
    public byte[] signature; // this is to prevent anybody from spending funds in our wallet

    public ArrayList<TransactionInput> inputs=new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs=new ArrayList<TransactionOutput>();

    private static int sequence =0;// a rough count of how many transactions have been generated

    //constructor
    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs){
        this.sender= from;
        this .reciepient=to;
        this.value=value;
        this.inputs=inputs;
    }

    //This calculates the transaction hash (which will be used as its id)
    private String calulateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(reciepient) +
                        Float.toString(value) + sequence
        );
    }
    //signs all the data we dont wish to be tampered with
    public void generateSignature(PrivateKey privateKey){
        String data=StringUtil.getStringFromKey(sender) +StringUtil.getStringFromKey(reciepient)+ Float.toString(value);
        signature=StringUtil.applyECDSASign(privateKey,data);
    }

    //verify the data wee signed hasnt been tampered with
    public boolean verifySignature(){
        String data=StringUtil.getStringFromKey(sender) +StringUtil.getStringFromKey(reciepient)+Float.toString(value);
        return StringUtil.verifyECDSASign(sender,data,signature);
    }


}
