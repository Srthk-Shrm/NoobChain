import axios from 'axios';

const API_BASE = 'http://localhost:8080/api';

export const createWallet = () => axios.post(`${API_BASE}/wallet`);
export const getBalance = (address) => axios.get(`${API_BASE}/wallet/${address}/balance`);
export const sendCoins = (from, to, amount) =>
  axios.post(`${API_BASE}/transaction`, { from, to, amount });
export const mineBlock = () => axios.post(`${API_BASE}/mine`);
export const getBlockchain = () => axios.get(`${API_BASE}/blockchain`); 