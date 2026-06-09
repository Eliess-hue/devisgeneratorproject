import {useNavigate} from "react-router-dom"
import { useAuth } from '../../context/AuthContext'

export default function UserProfile() {

    const { username, logout } = useAuth()

    const navigate = useNavigate()

    const handleLogout = () => {

        logout()

        navigate('/login')

    }

    return (

        <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-primary flex items-center justify-center text-primary-content font-bold">
                    {username?.charAt(0).toUpperCase()}
                </div>
                <span className="text-sm">{username}</span>
            </div>
            <button
                onClick={handleLogout}
                className="btn btn-xs btn-ghost text-error hover:bg-error/10 rounded-lg"
                style={{backgroundColor: '#450A0A'}}
                title="Se déconnecter"
            >
                ⏻
            </button>
        </div>

    )
}