package com.noobchain;
import java.security.PublicKey;

public class TransactionOutput {
    public String id;
    public PublicKey reciepient; //also known as the new owner of these coins.
    public float value; //amount of coins they own
    public String parentTransactionId; //the id of the transaction this output was created in

    //constructor
    public TransactionOutput(PublicKey reciepient,float value, String parentTransactionId){
        this.reciepient=reciepient;
        this.value=value;
        this.parentTransactionId=parentTransactionId;
        this.id=StringUtil.applySha256(StringUtil.getStringFromKey(reciepient)+Float.toString(value)+parentTransactionId);
    }
    //check if coin belongs to you
    public boolean isMine(PublicKey publicKey){
        return (publicKey==reciepient);
    }

}
