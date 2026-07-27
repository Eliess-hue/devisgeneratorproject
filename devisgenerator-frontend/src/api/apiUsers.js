import axiosConfig from "./axiosConfig.js"

export const getUsers = () =>
    axiosConfig.get("/api/users")

export const changeRole = (id, role) =>
    axiosConfig.put(`/api/users/${id}/role`, {
        role
    })