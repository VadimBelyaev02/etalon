package com.andersenlab.etalon.transactionservice.mapper;

import com.andersenlab.etalon.transactionservice.dto.template.response.TemplateInfoResponseDto;
import com.andersenlab.etalon.transactionservice.entity.PaymentEntity;
import com.andersenlab.etalon.transactionservice.entity.TemplateEntity;
import com.andersenlab.etalon.transactionservice.entity.TransferEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TemplateMapper {

  @Mapping(target = "templateId", source = "entity.templateId")
  @Mapping(target = "productId", source = "entity.productId")
  @Mapping(target = "description", source = "entity.description")
  @Mapping(target = "templateName", source = "entity.templateName")
  @Mapping(target = "amount", source = "entity.amount")
  @Mapping(target = "templateType", source = "entity.templateType")
  @Mapping(target = "createAt", source = "entity.createAt")
  @Mapping(target = "source", source = "entity.source")
  @Mapping(target = "destination", source = "entity.destination")
  TemplateInfoResponseDto templateEntityToDto(final TemplateEntity entity);

  @Mapping(target = "description", source = "payment.comment")
  @Mapping(target = "productId", source = "payment.paymentProductId")
  @Mapping(target = "destination", source = "payment.destination")
  TemplateEntity paymentToTemplate(PaymentEntity payment);

  List<TemplateInfoResponseDto> templateEntityToListOfDtos(final List<TemplateEntity> entities);

  @Mapping(target = "description", source = "transfer.comment")
  @Mapping(target = "productId", source = "transfer.transferTypeId")
  @Mapping(target = "source", source = "transfer.source")
  @Mapping(target = "destination", source = "transfer.destination")
  TemplateEntity transferToTemplate(TransferEntity transfer);
}
