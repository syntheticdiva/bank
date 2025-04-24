package com.java.bank.mapper;

import com.java.bank.dto.UserDTO;
import com.java.bank.entity.EmailData;
import com.java.bank.entity.PhoneData;
import com.java.bank.entity.User;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "emails", source = "emails", qualifiedByName = "emailsToSet")
    @Mapping(target = "phones", source = "phones", qualifiedByName = "phonesToSet")
    @Mapping(target = "balance", source = "account.balance")
    UserDTO toDto(User user);

    @Named("emailsToSet")
    default Set<String> emailsToSet(Set<EmailData> emails) {
        if (emails == null || !Hibernate.isInitialized(emails)) {
            return Collections.emptySet();
        }
        return emails.stream()
                .map(EmailData::getEmail)
                .collect(Collectors.toSet());
    }
//    @Named("emailsToSet")
//    default Set<String> emailsToSet(Set<EmailData> emails){
//        return emails.stream()
//                .map(EmailData::getEmail)
//                .collect(Collectors.toSet());
//    }
    @Named("phonesToSet")
    default Set<String> phonesToSet(Set<PhoneData> phones){
        return phones.stream()
                .map(PhoneData::getPhone)
                .collect(Collectors.toSet());
    }

}
