// src/services/career.service.ts

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-expect-error
import prisma from "../prisma/schema.prisma"
import { Career } from "@prisma/client"

// 모든 경력을 가져옵니다.
export const getAllCareers = async (): Promise<Career[]> => {
    return await prisma.career.findMany()
}

// 특정 경력을 ID로 가져옵니다.
export const getCareerById = async (id: number): Promise<Career | null> => {
    return await prisma.career.findUnique({
        where: { id },
    })
}

// 새 경력을 추가합니다.
export const createCareer = async (careerData: Omit<Career, "id" | "createdAt" | "updatedAt">): Promise<Career> => {
    return await prisma.career.create({
        data: careerData,
    })
}

// 경력을 업데이트합니다.
export const updateCareer = async (id: number, careerData: Partial<Career>): Promise<Career> => {
    return await prisma.career.update({
        where: { id },
        data: careerData,
    })
}

// 경력을 삭제합니다.
export const deleteCareer = async (id: number): Promise<Career> => {
    return await prisma.career.delete({
        where: { id },
    })
}
