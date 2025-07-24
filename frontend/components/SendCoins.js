import React, { useState } from 'react';
import { sendCoins } from '../api';
import { Button, Typography, TextField, Box } from '@mui/material';

export default function SendCoins() {
  const [from, setFrom] = useState('');
  const [to, setTo] = useState('');
  const [amount, setAmount] = useState('');
  const [status, setStatus] = useState('');
  const [error, setError] = useState('');

  const handleSend = async () => {
    setStatus('');
    setError('');
    try {
      const res = await sendCoins(from, to, amount);
      if (res.data.error) setError(res.data.error);
      else setStatus(res.data.status + ' (TxID: ' + res.data.txId + ')');
    } catch (e) {
      setError('Failed to send coins');
    }
  };

  return (
    <Box>
      <Typography variant="h5">Send Coins</Typography>
      <TextField label="Sender Address" value={from} onChange={e => setFrom(e.target.value)} fullWidth size="small" sx={{ my: 1 }} />
      <TextField label="Recipient Address" value={to} onChange={e => setTo(e.target.value)} fullWidth size="small" sx={{ my: 1 }} />
      <TextField label="Amount" value={amount} onChange={e => setAmount(e.target.value)} type="number" fullWidth size="small" sx={{ my: 1 }} />
      <Button variant="contained" onClick={handleSend} sx={{ mt: 1 }}>
        Send
      </Button>
      {status && <Typography color="primary" sx={{ mt: 1 }}>{status}</Typography>}
      {error && <Typography color="error" sx={{ mt: 1 }}>{error}</Typography>}
    </Box>
  );
} 