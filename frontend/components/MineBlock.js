import React, { useState } from 'react';
import { mineBlock } from '../api';
import { Button, Typography, Box } from '@mui/material';

export default function MineBlock() {
  const [status, setStatus] = useState('');
  const [blockHash, setBlockHash] = useState('');
  const [error, setError] = useState('');

  const handleMine = async () => {
    setStatus('');
    setBlockHash('');
    setError('');
    try {
      const res = await mineBlock();
      setStatus(res.data.status);
      setBlockHash(res.data.blockHash);
    } catch (e) {
      setError('Failed to mine block');
    }
  };

  return (
    <Box>
      <Typography variant="h5">Mine Block</Typography>
      <Button variant="contained" onClick={handleMine} sx={{ mt: 1 }}>
        Mine Block
      </Button>
      {status && <Typography color="primary" sx={{ mt: 1 }}>{status}</Typography>}
      {blockHash && <Typography sx={{ wordBreak: 'break-all' }}>Block Hash: {blockHash}</Typography>}
      {error && <Typography color="error" sx={{ mt: 1 }}>{error}</Typography>}
    </Box>
  );
} 