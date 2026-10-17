export interface AdoptionDTO {
    id: number,
    petId: number,
    petName: string,
    initialDate: string,
    endingDate?: string,
    status: string,
    ownerEmail?: string,
    adopterEmail?: string
}