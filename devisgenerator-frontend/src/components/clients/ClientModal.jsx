import {useState} from "react";

export default function ClientModal({
                                        isOpen,
                                        editingClient,
                                        name,
                                        setName,
                                        email,
                                        setEmail,
                                        phone,
                                        setPhone,
                                        address,
                                        setAddress,
                                        onSave,
                                        onClose
                                    }) {

    const [nameError, setNameError] = useState('')
    const [emailError, setEmailError] = useState('')
    const [phoneError, setPhoneError] = useState('')
    const [addressError, setAddressError] = useState('')

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

    if (!isOpen) {
        return null
    }

    const validateForm = () => {

        let valid = true

        if (!name.trim()) {
            setNameError('Le nom est obligatoire')
            valid = false
        } else {
            setNameError('')
        }

        if (!email.trim()) {

            setEmailError("L'email est obligatoire")
            valid = false

        } else if (!emailRegex.test(email)) {

            setEmailError('Format email invalide')
            valid = false

        } else {

            setEmailError('')

        }

        if (!phone.trim()) {
            setPhoneError('Le téléphone est obligatoire')
            valid = false
        } else {
            setPhoneError('')
        }

        if (!address.trim()) {
            setAddressError("L'adresse est obligatoire")
            valid = false
        } else {
            setAddressError('')
        }

        return valid

    }

    const handleSave = () => {

        if (!validateForm()) {
            return
        }

        resetErrors()
        onSave()

    }

    const resetErrors = () => {

        setNameError('')
        setEmailError('')
        setPhoneError('')
        setAddressError('')

    }

    return (
        <dialog className="modal modal-open">

            <div className="modal-box">

                <h3 className="font-bold text-lg mb-4">

                    {editingClient
                        ? 'Modifier le client'
                        : 'Nouveau client'
                    }

                </h3>

                <div className="flex flex-col gap-4">

                    <input
                        type="text"
                        placeholder="Nom"
                        className="input input-bordered"
                        value={name}
                        onChange={(e) => {

                            setName(e.target.value)

                            if (nameError) {
                                setNameError('')
                            }

                        }}
                    />
                    {nameError && (
                        <p
                            className="mt-1 text-sm"
                            style={{
                                color: '#FCA5A5'
                            }}
                        >
                            {nameError}
                        </p>
                    )}

                    <input
                        type="email"
                        placeholder="Email"
                        className="input input-bordered"
                        value={email}
                        onChange={(e) => {

                            setEmail(e.target.value)

                            if (emailError) {
                                setEmailError('')
                            }

                        }}
                    />
                    {emailError && (
                        <p
                            className="mt-1 text-sm"
                            style={{
                                color: '#FCA5A5'
                            }}
                        >
                            {emailError}
                        </p>
                    )}

                    <input
                        type="text"
                        placeholder="Téléphone"
                        className="input input-bordered"
                        value={phone}
                        onChange={(e) => {

                            setPhone(e.target.value)

                            if (phoneError) {
                                setPhoneError('')
                            }

                        }}
                    />
                    {phoneError && (
                        <p
                            className="mt-1 text-sm"
                            style={{
                                color: '#FCA5A5'
                            }}
                        >
                            {phoneError}
                        </p>
                    )}

                    <input
                        type="text"
                        placeholder="Adresse"
                        className="input input-bordered"
                        value={address}
                        onChange={(e) => {

                            setAddress(e.target.value)

                            if (addressError) {
                                setAddressError('')
                            }

                        }}
                    />
                    {addressError && (
                        <p
                            className="mt-1 text-sm"
                            style={{
                                color: '#FCA5A5'
                            }}
                        >
                            {addressError}
                        </p>
                    )}

                </div>

                <div className="modal-action">

                    <button
                        className="btn rounded-lg"
                        onClick={() => {

                            resetErrors()
                            onClose()

                        }}
                    >
                        Annuler
                    </button>

                    <button
                        className="btn btn-primary rounded-lg"
                        onClick={handleSave}
                    >
                        Enregistrer
                    </button>

                </div>

            </div>

        </dialog>
    )
}