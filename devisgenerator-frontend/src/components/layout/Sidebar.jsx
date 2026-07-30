import { NavLink } from 'react-router-dom'
import UserProfile from './UserProfile'
import {useAuth} from "../../context/AuthContext.jsx"
import logo from '../../assets/devgenerlogo.png'

export default function Sidebar() {

    const { role } = useAuth()

    const closeDrawer = () => {

        const drawer = document.getElementById('drawer-toggle');

        if (drawer) {
            drawer.checked = false;
        }

    }

    const navLinkClass = ({ isActive }) =>
        `
            flex items-center px-4 py-3 rounded-lg transition-colors
            ${
            isActive
                ? "bg-primary text-primary-content"
                : "hover:bg-base-300"
        }
        `;

    return (

        <aside className="w-64 min-h-full bg-base-200 border-r border-base-300 flex flex-col">

            {/* Logo */}
            <div className="p-6 flex items-center gap-3">

                <img src={logo} alt="DevisApp" className="h-32 w-auto"/>

            </div>

            {/* Navigation */}
            <nav className="flex-1 px-3">

                <NavLink
                    to="/dashboard"
                    onClick={closeDrawer}
                    className={navLinkClass}
                >
                    Dashboard
                </NavLink>

                <NavLink
                    to="/clients"
                    onClick={closeDrawer}
                    className={navLinkClass}
                >
                    Clients
                </NavLink>

                <NavLink
                    to="/quotes"
                    onClick={closeDrawer}
                    className={navLinkClass}
                >
                    Devis
                </NavLink>

                {role === "ROLE_ADMIN" && (

                    <NavLink
                        to="/users"
                        onClick={closeDrawer}
                        className={navLinkClass}
                    >
                        Utilisateurs
                    </NavLink>

                )}

            </nav>

            {/* Profil */}
            <div className="p-4 border-t border-base-300">

                <UserProfile />

            </div>

        </aside>

    )

}