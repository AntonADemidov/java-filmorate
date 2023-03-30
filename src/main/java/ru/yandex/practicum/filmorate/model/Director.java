package ru.yandex.practicum.filmorate.model;

import com.sun.istack.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Director {
    @NotNull
    private long id;
    private String name;
}
