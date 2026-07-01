import axiosConfig from './axiosConfig.js'

export const getClients = () =>
    axiosConfig.get('/api/clients')

export const createClient = (client) =>
    axiosConfig.post('/api/clients', client)

export const updateClient = (
    id,
    client
) =>
    axiosConfig.put(`/api/clients/${id}`, client)

export const deleteClient = (id) =>
    axiosConfig.delete(`/api/clients/${id}`)