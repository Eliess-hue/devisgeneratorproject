import { useState, useEffect } from 'react'

import {
    getClients,
    createClient,
    updateClient,
    deleteClient
} from '../api/apiClient.js'

import ClientModal from '../components/clients/ClientModal.jsx'
import ClientTable from '../components/clients/ClientTable.jsx'

export default function ClientsPage() {


const [clients, setClients] = useState([])
const [isModalOpen, setIsModalOpen] = useState(false)

const [editingClient, setEditingClient] = useState(null)

const [name, setName] = useState('')
const [email, setEmail] = useState('')
const [phone, setPhone] = useState('')
const [address, setAddress] = useState('')

const [search, setSearch] = useState('')
const [error, setError] = useState(null)

const filteredClients = clients.filter(
    client =>
        (client.name || '')
            .toLowerCase()
            .includes(search.toLowerCase())
)

const loadClients = async () => {

    try {

        const response = await getClients()

        setClients(response.data)
        setError(null)

    } catch (err) {

        console.error(err)

        setError(
            err.response?.data ||
            'Impossible de charger les clients'
        )

    }

}

const handleNewClient = () => {

    setEditingClient(null)

    setName('')
    setEmail('')
    setPhone('')
    setAddress('')

    setIsModalOpen(true)

}

const handleEditClient = (client) => {

    setEditingClient(client)

    setName(client.name)
    setEmail(client.email)
    setPhone(client.phone)
    setAddress(client.address)

    setIsModalOpen(true)

}

const handleSaveClient = async () => {

    try {

        if (editingClient) {

            await updateClient(
                editingClient.id,
                name,
                email,
                phone,
                address
            )

        } else {

            await createClient(
                name,
                email,
                phone,
                address
            )

        }

        await loadClients()
        setError(null)

        closeModal()

    } catch (err) {

        console.error(err)

        setError(
            err.response?.data ||
            'Impossible d’enregistrer le client'
        )

    }

}

const handleDeleteClient = async (id) => {

    const confirmed = window.confirm(
        'Supprimer ce client ?'
    )

    if (!confirmed) {
        return
    }

    try {

        await deleteClient(id)

        await loadClients()
        setError(null)

    } catch (err) {

        console.error(err)

        setError(
            err.response?.data ||
            'Impossible de supprimer le client'
        )

    }

}

const closeModal = () => {

    setIsModalOpen(false)

    setEditingClient(null)

    setName('')
    setEmail('')
    setPhone('')
    setAddress('')

}

    useEffect(() => {

        const fetchClients = async () => {
            await loadClients()
        }

        fetchClients()

    }, [])

return (
    <>

        <ClientModal
            isOpen={isModalOpen}
            editingClient={editingClient}
            name={name}
            setName={setName}
            email={email}
            setEmail={setEmail}
            phone={phone}
            setPhone={setPhone}
            address={address}
            setAddress={setAddress}
            onSave={handleSaveClient}
            onClose={closeModal}
        />

        <div className="space-y-6">

            {error && (
                <div
                    className="mb-4 rounded-lg border px-4 py-3"
                    style={{
                        backgroundColor: '#450A0A',
                        borderColor: '#7F1D1D',
                        color: '#FCA5A5'
                    }}
                >
                    <span>{error}</span>
                </div>
            )}

            <div className="flex items-center justify-between">

                <div>

                    <h2 className="text-3xl font-bold">
                        Clients
                    </h2>

                    <p className="text-base-content/60">
                        Gérez vos clients
                    </p>

                </div>

                <button
                    className="btn btn-primary rounded-lg"
                    onClick={handleNewClient}
                >
                    + Nouveau client
                </button>

            </div>

            <input
                type="text"
                placeholder="🔍 Rechercher un client..."
                className="input input-bordered w-full bg-base-200 border-base-300 rounded-lg"
                value={search}
                onChange={(e) =>
                    setSearch(e.target.value)
                }
            />

            <ClientTable
                clients={filteredClients}
                onEdit={handleEditClient}
                onDelete={handleDeleteClient}
            />

        </div>

    </>
)

}