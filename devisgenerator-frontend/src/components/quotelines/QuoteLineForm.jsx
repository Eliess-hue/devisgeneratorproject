export default function QuoteLineForm({
                                          lineForm,
                                          setLineForm,
                                          onAddLine,
                                          lineError
                                      }) {

    return (

        <div className="card bg-base-200 border border-base-300 rounded-lg">

            <div className="card-body">

                <h3 className="font-semibold text-lg">

                    Ajouter une ligne

                </h3>

                <div className="grid grid-cols-1 md:grid-cols-4 gap-3">

                    <input
                        type="text"
                        placeholder="Description"
                        className="input input-bordered rounded-lg"
                        value={lineForm.description}
                        onChange={(e) =>
                            setLineForm({
                                ...lineForm,
                                description: e.target.value
                            })
                        }
                    />

                    <input
                        type="number"
                        placeholder="Quantité"
                        className="input input-bordered rounded-lg"
                        value={lineForm.quantity}
                        onChange={(e) =>
                            setLineForm({
                                ...lineForm,
                                quantity: e.target.value
                            })
                        }
                    />

                    <input
                        type="number"
                        step="0.01"
                        placeholder="Prix unitaire"
                        className="input input-bordered rounded-lg"
                        value={lineForm.unitPrice}
                        onChange={(e) =>
                            setLineForm({
                                ...lineForm,
                                unitPrice: e.target.value
                            })
                        }
                    />

                    <button
                        className="btn btn-primary rounded-lg"
                        onClick={onAddLine}
                    >
                        Ajouter
                    </button>

                </div>

                {lineError && (

                    <div className="mb-4 mt-3 rounded-lg border px-4 py-3"
                         style={{
                             backgroundColor: '#450A0A',
                             borderColor: '#7F1D1D',
                             color: '#FCA5A5'
                         }}
                    >

                        <span>
                            {lineError}
                        </span>

                    </div>

                )}

            </div>

        </div>

    )

}