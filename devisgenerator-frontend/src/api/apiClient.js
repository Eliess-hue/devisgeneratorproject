import axiosConfig from './axiosConfig.js'

export const searchClients = (filters) => {

    const cleanFilters = Object.fromEntries(
        Object.entries(filters).filter(
            ([, value]) =>
                value !== null &&
                value !== undefined &&
                value !== ''
        )
    )

    return axiosConfig.get("/api/clients/search", {
        params: cleanFilters
    })
}

export const createClient = (client) =>
    axiosConfig.post('/api/clients', client)

export const updateClient = (
    id,
    client
) =>
    axiosConfig.put(`/api/clients/${id}`, client)

export const deleteClient = (id) =>
    axiosConfig.delete(`/api/clients/${id}`)