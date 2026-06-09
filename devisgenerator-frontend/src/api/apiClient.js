import axiosConfig from './axiosConfig.js'

export const getClients = () =>
    axiosConfig.get('/api/clients')

export const createClient = (
    name,
    email,
    phone,
    address
) =>
    axiosConfig.post('/api/clients', {
        name,
        email,
        phone,
        address
    })

export const updateClient = (
    id,
    name,
    email,
    phone,
    address
) =>
    axiosConfig.put(`/api/clients/${id}`, {
        name,
        email,
        phone,
        address
    })

export const deleteClient = (id) =>
    axiosConfig.delete(`/api/clients/${id}`)