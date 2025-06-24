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
    //returns true if new transaction could be created
    public boolean processTransaction(){
        if (verifySignature()==false){
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        //gather transaction inputs (make sure they are unspent):
        for(TransactionInput i: inputs){
            i.UTXO=NoobChain.UTXOs.get(i.transactionOutputId);
        }

        //Check if transaction is valid
        if(getInputsValue()< NoobChain.minimumTransaction){
            System.out.println("#Transaction Inputs too small: "+ getInputsValue());
            return false;
        }

        //generate transaction outputs
        float leftOver=getInputsValue()-value; //get value of inputs then the leftover change
        transactionId=calulateHash();
        outputs.add(new TransactionOutput(this.reciepient,value,transactionId));//send value to recipient
        outputs.add(new TransactionOutput(this.sender,leftOver,transactionId));// send the left over 'change' to sender

        //add outputs to unspent list
        for(TransactionOutput o: outputs){
            NoobChain.UTXOs.put(o.id,o);
        }

        //remove transaction inputs from UTXOs list as spent:
        for (TransactionInput i:inputs){
            if(i.UTXO==null) continue; //if transaction can't be found skip it
            NoobChain.UTXOs.remove(i.UTXO.id);
        }
        return true;
    }

    //return sum of inputs(UTXOs) values
    public float getInputsValue(){
        float total=0;
        for(TransactionInput i: inputs){
            if(i.UTXO==null) continue;//if transaction can't be found skip it
            total+=i.UTXO.value;

        }
        return total;
    }
    //return sum of outputs
    public float getOutputsValue(){
        float total=0;
        for(TransactionOutput o: outputs){
            total+=o.value;
        }
        return total;
    }



}
