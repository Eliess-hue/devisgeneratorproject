export default function ClientTable({
                                        clients,
                                        onEdit,
                                        onDelete
                                    }) {

    if (clients.length === 0) {

        return (

            <div className="card bg-base-200 border border-base-300 rounded-lg">

                <div className="card-body text-center py-10">

                    <h3 className="font-semibold text-lg">
                        Aucun client enregistré
                    </h3>

                    <p className="text-base-content/60 mt-2">
                        Commencez par créer votre premier client.
                    </p>

                </div>

            </div>

        )

    }

    return (
        <div className="card bg-base-200 border border-base-300 rounded-lg">

            <div className="card-body">

                <h3 className="font-semibold">
                    Mes clients
                </h3>

                <div className="overflow-x-auto">

                    <table className="table">

                        <thead>
                        <tr>
                            <th>Nom</th>
                            <th>Email</th>
                            <th>Téléphone</th>
                            <th>Nb devis</th>
                            <th>Dernier devis</th>
                            <th>Actions</th>
                        </tr>
                        </thead>

                        <tbody>

                        {clients.map(client => (

                            <tr key={client.id}>

                                <td>{client.name}</td>
                                <td>{client.email}</td>
                                <td>{client.phone}</td>
                                <td>{client.quoteCount}</td>

                                <td>
                                    {client.lastQuoteNumber === "-"
                                        ? "Aucun devis"
                                        : client.lastQuoteNumber}
                                </td>

                                <td>

                                    <button
                                        className="btn btn-xs bg-blue-900 text-blue-400 border-none hover:bg-blue-800 rounded-lg"

                                        onClick={() =>
                                            onEdit(client)
                                        }
                                    >
                                        Modifier
                                    </button>

                                    |

                                    <button
                                        className="btn btn-xs bg-red-950 text-red-300 border-none hover:bg-red-900 rounded-lg"
                                        onClick={() =>
                                            onDelete(client.id)
                                        }
                                    >
                                        Supprimer
                                    </button>

                                </td>

                            </tr>

                        ))}

                        </tbody>

                    </table>

                </div>

            </div>

        </div>
    )
}