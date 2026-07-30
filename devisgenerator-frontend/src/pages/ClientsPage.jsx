import { useState, useEffect } from 'react'

import useClients from "../hooks/useClients.js";

import Pagination from "../components/common/Pagination.jsx"
import ClientModal from '../components/clients/ClientModal.jsx'
import ClientTable from '../components/clients/ClientTable.jsx'
import ConfirmationModal from "../components/common/ConfirmationModal.jsx"
import ClientsPageSkeleton from "../components/skeletons/ClientsPageSkeleton.jsx"
import Alert from "../components/common/Alert.jsx"

export default function ClientsPage() {


    const [isModalOpen, setIsModalOpen] = useState(false)
    const [editingClient, setEditingClient] = useState(null)
    const [name, setName] = useState('')
    const [email, setEmail] = useState('')
    const [phone, setPhone] = useState('')
    const [address, setAddress] = useState('')
    const [search, setSearch] = useState('')
    const [isDeleteModalOpen, setIsDeleteModalOpen] =
         useState(false)
    const [clientToDelete, setClientToDelete] =
         useState(null)

    const {
        clients,
        loading,
        isSearching,
        error,
        totalPages,
        totalElements,
        loadClients,
        saveClient,
        removeClient
    } = useClients()

    const [page, setPage] = useState(0)

    const refresh = () =>
        loadClients({

            search,
            page,
            size: 10

        })

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

            await saveClient({

                id: editingClient?.id,
                name,
                email,
                phone,
                address

            });

            await refresh();

            closeModal();

        } catch (err) {

            console.error(err)

        }

    }

    const handleDeleteClient = (id) => {

        setClientToDelete(id)

        setIsDeleteModalOpen(true)

    }

    const handleConfirmDelete = async () => {

        try {

            await removeClient(clientToDelete);

            await refresh();

            closeDeleteModal();

        } catch (err) {

            console.error(err)

        }

    }

    const closeDeleteModal = () => {

        setIsDeleteModalOpen(false)

        setClientToDelete(null)

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

        setPage(0)

    }, [search])

    useEffect(() => {

        const timer = setTimeout(() => {

            refresh()

        }, 300)

        return () => clearTimeout(timer)

    }, [search, page])

    if (loading) {
        return <ClientsPageSkeleton />
    }

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

            <ConfirmationModal
                isOpen={isDeleteModalOpen}
                title="Supprimer le client"
                message="Voulez-vous vraiment supprimer ce client ?"
                onConfirm={handleConfirmDelete}
                onClose={closeDeleteModal}
                confirmLabel="Supprimer"
            />

            <div className="space-y-6">

                {error && (
                    <Alert
                        type="error"
                        className="mb-4"
                    >
                        {error}
                    </Alert>
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
                    clients={clients}
                    onEdit={handleEditClient}
                    onDelete={handleDeleteClient}
                />

                <Pagination
                    page={page}
                    totalPages={totalPages}
                    totalElements={totalElements}
                    onPageChange={setPage}
                    label="clients"
                />

            </div>

        </>
    )

}