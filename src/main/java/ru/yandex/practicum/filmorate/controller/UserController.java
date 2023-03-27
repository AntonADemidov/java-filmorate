package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final String actionWithFriends = "/{id}/friends/{friendId}";

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws Exception {
        return userService.createUser(user);
    }

    /*Никита, добрый день!

    Первоначально за счет аннотации @RequestBody объект в формате Json из тела запроса автоматически конвертируется
    в Java-объект класса User (в этот момент объект создается через конструктор Lombok, вызываемый аннотацией @Data
    (по факту применяется аннотация @RequiredArgsConstructor, входящая в объединенный аннотацией @Data пул аннотация Lombok).

    Далее объект передается в метод createUser(User User) класса UserService, а оттуда вметод createUser(User User)
    класса UserDbStorage. В классе UserDbStorage первоначально происходит валидация данных (полей объекта) с помощью приватного метода
    validateUser(User User), отвечающего за соответствие данных в полях объекта заявленным в ТЗ условиям. В качестве
    результата работы метода либо пробрасывается ValidationException с сообщением о необходимом формате данных (тогда
    создание объекта прекращается и в базу данных он не сохраняется), либо, если все данные соответствуют условиям,
    методом возвращается соответствующий условиям объект User.

    Далее прошедший валидацию объект получает id и передается в метод createUser(User user) класса UserDaoImpl,
    где сначала с помощью sql-запросов сохраняется в базу данных, а после заново воссоздается из сохраненных в базе данных
    и возвращается обратно в UserController по той же цепочке классов и createUser-методов уже в качестве ответа сервера
    на первоначальный запрос.

    Аналогичный путь проходят и объекты класса Film при вызове запросом к серверу соответствующего метода createFilm(Film Film)
    класса FilmController.

    Буду благодарен за обратную связь, и да пребудет с вами сила!*/
    @PutMapping
    public User updateUser(@RequestBody User user) throws Exception {
        return userService.updateUser(user);
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @PutMapping(actionWithFriends)
    public void addFriend(@PathVariable long id, @PathVariable long friendId) throws DataAlreadyExistException {
        userService.addFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        return userService.getFriends(id);
    }

    @DeleteMapping(actionWithFriends)
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) throws DataAlreadyExistException {
        userService.removeFriend(id, friendId);
    }
}