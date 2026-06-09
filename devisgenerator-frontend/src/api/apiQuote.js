import axiosConfig from './axiosConfig.js'

export const getQuotes = () =>
    axiosConfig.get('/api/quotes')

export const createQuote = (
    clientId,
    status
) =>
    axiosConfig.post('/api/quotes', {
        clientId,
        status
    })

export const updateQuote = (
    id,
    clientId,
    status
) =>
    axiosConfig.put(`/api/quotes/${id}`, {
        clientId,
        status
    })

export const deleteQuote = (id) =>
    axiosConfig.delete(`/api/quotes/${id}`)

// QuoteLine
export const getQuoteLines = (quoteId) =>
    axiosConfig.get(`/api/quotes/${quoteId}/lines`)

export const addQuoteLine = (quoteId, line) =>
    axiosConfig.post(`/api/quotes/${quoteId}/lines`, line)

export const deleteQuoteLine = (
    quoteId,
    lineId
) =>
    axiosConfig.delete(
        `/api/quotes/${quoteId}/lines/${lineId}`
    )

export const getQuoteById = (id) =>
    axiosConfig.get(`/api/quotes/${id}`)