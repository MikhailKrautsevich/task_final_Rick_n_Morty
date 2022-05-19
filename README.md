# task_final_Rick_n_Morty
Финальный курсовой проект для Aston по м/ф "Рик и Морти"

История задач:
    - Network, json, GSON, Retrofit
    - SQlite, Room
    - MVVM, Clean Architecture
    - RxJava
    - Dagger2 (очень ограниченно, заинжекчены только Picasso, NetworkChecker и Api)

Задание: Разработка Android приложения для работы с rickandmortyapi.com
Язык разработки: Котлин 
Архитектура: MVVM
Работа с многопоточностью: RxJava
Permissions: android.permission.INTERNET,
             android.permission.ACCESS_NETWORK_STATE

Использованные библиотеки:
- Splashscreen: androidx.core:core-splashscreen:1.0.0-beta02
- Пикассо: com.squareup.picasso:picasso:2.71828
- Ретрофит: implementation 'com.squareup.retrofit2:retrofit:2.9.0'
            implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
            implementation 'com.squareup.okhttp3:okhttp:4.8.0'
            implementation 'com.squareup.okhttp3:logging-interceptor:4.8.0'
            com.squareup.retrofit2:adapter-rxjava3:2.9.0
- RxJava3:  io.reactivex.rxjava3:rxandroid:3.0.0
            io.reactivex.rxjava3:rxjava:3.0.9
- Pull to refresh: androidx.swiperefreshlayout:swiperefreshlayout:1.1.0
- Room:     implementation "androidx.room:room-ktx:2.4.2
            implementation("androidx.room:room-runtime:2.4.2
            annotationProcessor("androidx.room:room-compiler:2.4.2
            androidTestImplementation "androidx.room:room-testing:2.4.2
            androidx.room:room-compiler:2.4.2
            androidx.room:room-rxjava3:2.4.2
- Dagger2:  com.google.dagger:dagger:2.35.1
            'com.google.dagger:dagger-compiler:2.35.1


Разделение на слои согласно Clean Architecture:
- Data:
    Room,
    Retrofit,
    DataProviders
- Domain
    отсутствует
- Presentation:
    MainActivity,
    Fragments,
    ViewModels

UI:
- SplashScreen: При запуске приложения показывается SplashScreen (androidx.core:core-splashscreen:1.0.0-beta02)
- Навигация по приложению: Далее стартует приложение. Показывается единственное активити (вся навигация
планируется во фрагментах). Внизу фрагментов располагается BottomNavigationView с иконками для перехода
на фрагменты с персонажами, с локациями, эпизодами сериала "Рик и Морти". Первый отображаемый фрагмент - 
вкладка с персонажами. Транзакция добавляется в backstack, поддерживается "навигация назад" через popBackStack.
Кнопка ведущая на текущий фрагмент блокируется, т. е. если пользователь находится на экране со списком
персонажей, он не сможет нажать на кнопку ведущую к списку персонажей.
- Стрелка для навигации назад отображается после перехода по след. фрагмент, то есть изначально не
видна.
- Для отображения списков используется RecyclerView. Для обновления данных используется DiffUtils.
- ViewHolder: Слева сположена TextView c названием и (для персонажей картинка), справа - всё остальное.
"Всё остальное" предполагаю отображать в единственном TextView через переопределённый toString() data class'а.
По нажатию на ViewHolder переход на фрагмент с деталями, отображенного элемента. 
- Picasso: Для загрузки и кеширования изображений используется Picasso.
- Пагинация: Самописная реализация через OnScrollListener. Пагинация осуществляется только при работе с интернетом,
при использовании сохраненных данных данные грузятся сразу все целиком.
- Поиск: Через SearchView в ActionBar. Поиск осуществляетсятлько по полю Name дата-классов. При
использования поиска отправляется новый запрос, а не сортировка-фильтрация уже загруженного списка.
- Фильтрация: Кнопка "Show filters" отображает доступные для вкладки со списками фильтры:
например для фрагмента со списком персонажей это имя (вводится в EditText), статус (выбирается в Spinner из
вариантов alive, dead или unknown), тип (вводится в EditText), вид (вводится в EditText), пол (выбирается
в Spinner из вариантов female, male, genderless или unknown).
- Pull-to-Refresh: Все фрагменты поддерживают Pull-to-Refresh. (Проверяет наличие интернета, и, если
интернет доступен, то через ViewModel получает данные из интернета.)
- ProgressBar: присутствует в каждом фрагменте, как со списком, так и с деталями; в обычном состоянии invisible, 
при загрузке данных - visible.

ViewModel:
- Наследуется от ViewModel.
- Создается в момент создания фрагмента. Для каждого фрагмента отдельная ViewModel.
- Служит для сохранения данных во время пересоздания activity при изменении ориентации экрана. Основное назначение
Livedata'ы - связь Repository и UI.
- Напрямую взаимодействует с Repository.
- При уничтожении закрывает ViewModel вызывает dispose у выполняемых Rx-вызовов (цепочек методов).

  Пример ViewModel для фрагмента со списком:
  -- поле со ссылкой на репозиторий
  -- поле с Livedata для списка
  -- поле с Livedata для ProgressBar
  -- функция для Pull-to-Refresh, которая "дергает" репозиторий и пробует получить оттуда новые данные.
     
  Пример ViewModel для фрагмента с деталями:
  -- поле со ссылкой на репозиторий
  -- поле с Livedata для контента в виде data класса.
  -- поле с Livedata для ProgressBar
  -- функция для Pull-to-Refresh, которая "дергает" репозиторий и пробует получить оттуда новые данные.
  
Repository:
- Класс для непосредственного получения данных. Инкапсулирует всю логику для получения данных.
- При наличии интернета работает с данными из сети, с кешированными - только при отсутствии интернет-
соединения. Кеширование изображений осуществляется при помощи Picasso.
- проверяет наличие интернет-соединения: если интернет доступен, то работает с интернетом, иначе - с
RoomDataBase.
- Кеширование: При загрузке данных из Интернет репозиторий проверяет наличие этих данных в Room, и если нет, 
то вносит данные в DB.
- RoomDataBase оздается по принципу singleton.

Retrofit:
- используется для получения данных из интернета. 
- BASE_URL: https://rickandmortyapi.com/api/ .
- получаемый формат данных: json.
- GSON: для парсинга json в data классы используется бииблиотека GSON

RoomDataBase:
- состоит из трёх таблиц: сharacters, locations, episodes.
- dao объекты: три dao, по одному на каждую таблицу.

Схема работы приложения:

Запуск:                 отображение SplashScreen ->
                        появляется MainActivity ->
                        создается стартовый фрагмент со списком персонажей ->
                        ProgressBar.visibility = Visibility.Visible (загрузка контента) ->
                        создается ViewModel ->
                        ViewModel проверяет наличие интернет соединения ->
                        соединение недоступно:        данные поступают из RoomDataBase --->
                        соединение доступно:          Retrofit-запрос --->
                                                      получение json --->
                                                      парсинг с помощью gson --->
                        полученные данные помещаются в LiveData ->
                        отображение содержимого во фрагменте->
                        ProgressBar.visibility = Visibility.Invisible (загрузка контента закончена)
                        далее для фрагмента с детальной информацией:
                                                    - после загрузки данных начинается загрузка списка
                                                    - появляется ProgressBar для списка
                                                    - проверка наличия интернет соединения
                                                    - преобразование списка из RetrofitModel или Entity в Data 
                                                    - список отправляется в Recycler через Livedata
                                                    - ProgressBar для списка не виден
                                                    
Подробное описание UI (фрагментов):

Фрагменты можно разделить на фрагменты со списком элементов и с детальной информацией об элементе.
К фрагментам со списком относятся:
 - CharactersListFragment
 - EpisodeListFragmen
 - LocationListFragment
К фрагментам с детальной информацией:
 - CharacterDetailFragment
 - EpisodeDetailFragment
 - LocationDetailFragment

Описание UI для фрагмента со списком:
- собственно список в RecyclerView. После нажатия на элемент списка пользователь попадает на экран с детальной информацией
- SearchView для поиска по именам-названиям списка. Поиск осуществляется после ввода минимум 3ех символов.
- Textview, уведоимляющее об отсутствии данных для отображения. Может появиться при фильтрации или поиске.
- Стрелка для навигации назад, появится после первого перехода пользователнем н адругой экран, т. е. изначально не видна.
- BottomNavigationView, для перехода на другие фрагменты со списками.
- Кнопка "Show/Hide filters" для отображения фильтров.
- Кнопка "Use filters" дл применения фильтров
- EditText и Spinner'ы для ввода значений для фильтрации
- ProgressBar для отображения процесса загрузки
- ProgressBar для отображения процесса загрузки при пагинации (находится слева от кнопки "Show/Hide filters")
- SwipeRefreshLayout для Pull-to-Refresh

Описание UI для фрагмента с детальной информацией:
- TextView и Imageview для отображения детальной информации. Такие поля как id, created и url я посчитал системными и отображать не стал.
- список в RecyclerView, например для CharacterDetailFragment это будет список серий, в которых появляется персонаж. После нажатия на элемент списка пользователь попадает на экран с детальной информацией об серии.
- Стрелка для навигации назад.
- BottomNavigationView, для перехода на фрагменты со списками.
- ProgressBar для отображения процесса загрузки детальных данных.
- ProgressBar для отображения процесса загрузки при списка.
- SwipeRefreshLayout для Pull-to-Refresh
