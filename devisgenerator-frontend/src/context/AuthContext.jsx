import {
    createContext,
    useContext,
    useEffect,
    useState
} from "react"

import {getMe} from "../api/apiAuth.js"

const AuthContext = createContext(null)

export function AuthProvider({ children }) {

    const [id, setId] = useState(null)

    const [token, setToken] = useState(
        localStorage.getItem("token") || null
    )

    const [username, setUsername] = useState(null)

    const [role, setRole] = useState(null)

    const [isLoading, setIsLoading] = useState(true)

    const saveAuth = (
        newId,
        newToken,
        newUsername,
        newRole
    ) => {

        localStorage.setItem(
            "token",
            newToken
        )

        setId(newId)
        setToken(newToken)
        setUsername(newUsername)
        setRole(newRole)

    }

    const logout = () => {

        localStorage.removeItem("token")

        setId(null)
        setToken(null)
        setUsername(null)
        setRole(null)

    }

    useEffect(() => {

        if (!token) {

            setIsLoading(false)

            return

        }

        getMe(token)
            .then(response => {

                setId(response.data.id)
                setUsername(response.data.username)
                setRole(response.data.role)

            })
            .catch(() => {

                logout()

            })
            .finally(() => {

                setIsLoading(false)

            })

    }, [])

    return (

        <AuthContext.Provider
            value={{
                id,
                token,
                username,
                role,
                isLoading,
                saveAuth,
                logout
            }}
        >

            {children}

        </AuthContext.Provider>

    )

}

export const useAuth = () => useContext(AuthContext)