package org.bhel.hrm.server.mapper;

import org.bhel.hrm.common.dtos.BenefitPlanDTO;
import org.bhel.hrm.server.domain.BenefitPlan;

public final class BenefitPlanMapper {
    private BenefitPlanMapper() {
        throw new UnsupportedOperationException("This class BenefitPlanMapper is a utility class; it should not be instantiated.");
    }

    public static BenefitPlanDTO mapToDto(BenefitPlan domain) {
        if (domain == null)
            return null;

        return new BenefitPlanDTO(
            domain.getId(),
            domain.getPlanName(),
            domain.getProvider(),
            domain.getDescription(),
            domain.getCostPerMonth()
        );
    }

    public static BenefitPlan mapToDomain(BenefitPlanDTO dto) {
        if (dto == null)
            return null;

        return new BenefitPlan(
            dto.id(),
            dto.planName(),
            dto.provider(),
            dto.description(),
            dto.costPerMonth()
        );
    }
}
