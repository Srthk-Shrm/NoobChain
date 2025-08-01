package com.noobchain;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;

public class StringUtil {
    public static String applySha256(String input){
        try{
            MessageDigest digest=MessageDigest.getInstance("SHA-256");
            //applies sha256 to our input
            byte[] hash=digest.digest(input.getBytes("UTF-8"));
            StringBuffer hexString=new StringBuffer();

            for(int i=0;i<hash.length;i++){
                String hex=Integer.toHexString(0xff & hash[i]);
                if(hex.length()==1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }

    }
    public static byte[] applyECDSASign(PrivateKey privateKey, String input){
        Signature dsa;
        byte[] output=new byte[0];
        try {
            dsa=Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte=input.getBytes();
            dsa.update(strByte);
            byte[] realSign=dsa.sign();
            output=realSign;
        } catch (Exception e){
            throw new RuntimeException();
        }
        return output;
    }

    public static boolean verifyECDSASign(PublicKey publicKey, String data, byte[] signature){
        try{
            Signature ecdsaVerify=Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        } catch (Exception e){
            throw new RuntimeException();
        }
    }

    //short hand helper to turn Object into Json string
    public static String getJson(Object o){
        return new GsonBuilder().setPrettyPrinting().create().toJson(o);
    }

    //returns difficulty string target, to compare to hash. eg difficulty of 5 will return "00000"
    public static String getDifficultyString(int difficulty){
        return new String(new char[difficulty]).replace('\0','0');
    }

    public static String getStringFromKey(Key key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    //tacks in array of transactions and returns a mekle root
    public static String getMerkleRoot(ArrayList<Transaction> transactions){
        int count= transactions.size();;
        ArrayList<String> previousTreeLayer=new ArrayList<String>();
        for(Transaction transaction:transactions){
            previousTreeLayer.add(transaction.transactionId);
        }
        ArrayList<String> treeLayer=previousTreeLayer;
        while(count>1){
            treeLayer=new ArrayList<String>();
            for(int i=1;i<previousTreeLayer.size();i++){
                treeLayer.add(applySha256(previousTreeLayer.get(i-1)+previousTreeLayer.get(i)));

            }
            count= treeLayer.size();
            previousTreeLayer=treeLayer;
        }
        String merkleRoot=(treeLayer.size()==1)? treeLayer.get(0):"";
        return merkleRoot;
    }
}
