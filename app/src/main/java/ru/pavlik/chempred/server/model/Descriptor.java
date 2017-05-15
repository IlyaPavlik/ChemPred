package ru.pavlik.chempred.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.pavlik.chempred.client.model.dao.DescriptorType;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Descriptor implements Serializable {

    @Id
    private String name;
    @Enumerated(EnumType.STRING)
    private DescriptorType type;
}
