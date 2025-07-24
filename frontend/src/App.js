import React from 'react';
import Wallet from './components/Wallet';
import SendCoins from './components/SendCoins';
import MineBlock from './components/MineBlock';
import BlockchainExplorer from './components/BlockchainExplorer';
import { Container, Typography, Divider } from '@mui/material';

function App() {
  return (
    <Container maxWidth="md">
      <Typography variant="h3" align="center" gutterBottom>
        NoobChain Frontend
      </Typography>
      <Wallet />
      <Divider sx={{ my: 2 }} />
      <SendCoins />
      <Divider sx={{ my: 2 }} />
      <MineBlock />
      <Divider sx={{ my: 2 }} />
      <BlockchainExplorer />
    </Container>
  );
}

export default App;
