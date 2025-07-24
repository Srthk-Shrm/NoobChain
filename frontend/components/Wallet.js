import React, { useState } from 'react';
import { createWallet, getBalance } from '../api';
import { Button, Typography, TextField, Box } from '@mui/material';

export default function Wallet() {
  const [address, setAddress] = useState('');
  const [balance, setBalance] = useState(null);
  const [error, setError] = useState('');

  const handleCreate = async () => {
    setError('');
    try {
      const res = await createWallet();
      setAddress(res.data.address);
      setBalance(res.data.balance);
    } catch (e) {
      setError('Failed to create wallet');
    }
  };

  const handleCheckBalance = async () => {
    setError('');
    try {
      const res = await getBalance(address);
      if (res.data.error) setError(res.data.error);
      else setBalance(res.data.balance);
    } catch (e) {
      setError('Failed to fetch balance');
    }
  };

  return (
    <Box>
      <Typography variant="h5">Wallet</Typography>
      <Button variant="contained" onClick={handleCreate} sx={{ mt: 1 }}>
        Create Wallet
      </Button>
      {address && (
        <Box sx={{ mt: 2 }}>
          <Typography variant="body1">Address:</Typography>
          <TextField value={address} fullWidth InputProps={{ readOnly: true }} size="small" sx={{ mb: 1 }} />
          <Button variant="outlined" onClick={handleCheckBalance} sx={{ mb: 1 }}>
            Check Balance
          </Button>
          {balance !== null && <Typography>Balance: {balance}</Typography>}
        </Box>
      )}
      {error && <Typography color="error">{error}</Typography>}
    </Box>
  );
} 