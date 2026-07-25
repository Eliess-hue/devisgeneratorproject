import { Outlet } from 'react-router-dom'

import Sidebar from '../components/layout/Sidebar'
import Topbar from '../components/layout/Topbar'

export default function Layout() {

    return (

        <div className="drawer lg:drawer-open">

            <input
                id="drawer-toggle"
                type="checkbox"
                className="drawer-toggle"
            />

            <div className="drawer-content flex flex-col min-h-screen bg-base-100">

                <Topbar />

                <main className="flex-1 p-8">

                    <label
                        htmlFor="drawer-toggle"
                        className="btn btn-ghost btn-square lg:hidden mb-4 rounded-lg"
                    >

                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            fill="none"
                            viewBox="0 0 24 24"
                            strokeWidth="2"
                            stroke="currentColor"
                            className="w-6 h-6"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                d="M4 6h16M4 12h16M4 18h16"
                            />
                        </svg>

                    </label>

                    <Outlet />

                </main>

            </div>

            <div className="drawer-side">

                <label
                    htmlFor="drawer-toggle"
                    aria-label="close sidebar"
                    className="drawer-overlay"
                />

                <Sidebar />

            </div>

        </div>

    )

}