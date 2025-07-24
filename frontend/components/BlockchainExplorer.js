import React, { useEffect, useState } from 'react';
import { getBlockchain } from '../api';
import { Typography, Box, Paper, List, ListItem, ListItemText } from '@mui/material';

export default function BlockchainExplorer() {
  const [blocks, setBlocks] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchBlocks();
  }, []);

  const fetchBlocks = async () => {
    setError('');
    try {
      const res = await getBlockchain();
      setBlocks(res.data);
    } catch (e) {
      setError('Failed to fetch blockchain');
    }
  };

  return (
    <Box>
      <Typography variant="h5">Blockchain Explorer</Typography>
      {error && <Typography color="error">{error}</Typography>}
      <List>
        {blocks.map((block, idx) => (
          <Paper key={block.hash} sx={{ my: 1, p: 2 }}>
            <Typography variant="subtitle1">Block #{idx}</Typography>
            <Typography variant="body2" sx={{ wordBreak: 'break-all' }}>Hash: {block.hash}</Typography>
            <Typography variant="body2" sx={{ wordBreak: 'break-all' }}>Previous Hash: {block.previousHash}</Typography>
            <Typography variant="body2">Transactions:</Typography>
            <List sx={{ pl: 2 }}>
              {block.transactions.map((tx, tIdx) => (
                <ListItem key={tx.transactionId || tIdx} sx={{ display: 'block' }}>
                  <ListItemText
                    primary={`TxID: ${tx.transactionId}`}
                    secondary={
                      <>
                        <div>From: {tx.sender}</div>
                        <div>To: {tx.reciepient}</div>
                        <div>Amount: {tx.value}</div>
                      </>
                    }
                  />
                </ListItem>
              ))}
            </List>
          </Paper>
        ))}
      </List>
    </Box>
  );
} 