export default function QuoteClientCard({
                                            client
                                        }) {

    return (

        <div className="card bg-base-200 border border-base-300 rounded-lg">

            <div className="card-body">

                <h3 className="font-semibold">

                    Informations client

                </h3>

                <div className="space-y-2">

                    <div className="flex justify-between">

                        <span className="text-base-content/60">
                            Nom
                        </span>

                        <span>
                            {client.name}
                        </span>

                    </div>

                    <div className="flex justify-between">

                        <span className="text-base-content/60">
                            Email
                        </span>

                        <span>
                            {client.email}
                        </span>

                    </div>

                    <div className="flex justify-between">

                        <span className="text-base-content/60">
                            Téléphone
                        </span>

                        <span>
                            {client.phone}
                        </span>

                    </div>

                    <div className="flex justify-between">

                        <span className="text-base-content/60">
                            Adresse
                        </span>

                        <span>
                            {client.address}
                        </span>

                    </div>

                </div>

            </div>

        </div>

    )

}