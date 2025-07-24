package com.noobchain.api;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.noobchain.*;
import org.springframework.web.bind.annotation.*;
import java.security.PublicKey;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")

public class BlockchainController {

    // In-memory wallets for demo (address: Wallet)
    private final Map<String, Wallet> wallets = new HashMap<>();

    public BlockchainController() {
        if (NoobChain.blockchain.isEmpty()) {
            // Setup security provider and genesis block
            NoobChain.main(new String[]{});
        }
    }

    @PostMapping("/wallet")
    public Map<String, Object> createWallet() {
        Wallet wallet = new Wallet();
        String address = StringUtil.getStringFromKey(wallet.publicKey);
        wallets.put(address, wallet);
        Map<String, Object> resp = new HashMap<>();
        resp.put("address", address);
        resp.put("balance", wallet.getBalance());
        return resp;
    }

    @GetMapping("/wallet/{address}/balance")
    public Map<String, Object> getBalance(@PathVariable String address) {
        Wallet wallet = wallets.get(address);
        Map<String, Object> resp = new HashMap<>();
        if (wallet == null) {
            resp.put("error", "Wallet not found");
            return resp;
        }
        resp.put("address", address);
        resp.put("balance", wallet.getBalance());
        return resp;
    }

    @PostMapping("/transaction")
    public Map<String, Object> sendCoins(@RequestBody Map<String, Object> body) {
        String from = (String) body.get("from");
        String to = (String) body.get("to");
        double amount = Double.parseDouble(body.get("amount").toString());
        Wallet sender = wallets.get(from);
        Wallet recipient = wallets.get(to);
        Map<String, Object> resp = new HashMap<>();
        if (sender == null || recipient == null) {
            resp.put("error", "Sender or recipient wallet not found");
            return resp;
        }
        Transaction tx = sender.sendFunds(recipient.publicKey, (float) amount);
        if (tx == null) {
            resp.put("error", "Not enough funds");
            return resp;
        }
        Block block = new Block(NoobChain.blockchain.get(NoobChain.blockchain.size() - 1).hash);
        block.addTransaction(tx);
        NoobChain.addBlock(block);
        resp.put("status", "Transaction added and block mined");
        resp.put("txId", tx.transactionId);
        return resp;
    }

    @PostMapping("/mine")
    public Map<String, Object> mineBlock() {
        Block block = new Block(NoobChain.blockchain.get(NoobChain.blockchain.size() - 1).hash);
        NoobChain.addBlock(block);
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "Block mined");
        resp.put("blockHash", block.hash);
        return resp;
    }

    @GetMapping("/blockchain")
    public List<Block> getBlockchain() {
        return NoobChain.blockchain;
    }
} 