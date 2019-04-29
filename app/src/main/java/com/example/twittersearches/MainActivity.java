package com.example.twittersearches;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Имя файла SharedPreferences с сохраненными запросами
    private static final String SEARCHES = "searches";
    private EditText queryEditText; // Для ввода запроса
    private EditText tagEditText; // Для ввода тега
    private FloatingActionButton saveFloatingActionButton; // Для сохранения
    private SharedPreferences savedSearches; // Сохраненные запросы
    private List<String> tags; // Список тегов сохраненных запросов
    private SearchesAdapter adapter; // Для связывания данных с RecyclerView
    // Настройка графического интерфейса и регистрация слушателей
        @Override
        protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
           setContentView(R.layout.activity_main);
           Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
           setSupportActionBar(toolbar);
    // Получить ссылки на EditText и добавить слушателей
           queryEditText = ((TextInputLayout) findViewById(
              R.id.queryTextInputLayout)).getEditText();
           queryEditText.addTextChangedListener(textWatcher);
           tagEditText = ((TextInputLayout) findViewById(
              R.id.tagTextInputLayout)).getEditText();
          tagEditText.addTextChangedListener(textWatcher);
    // Получить объект SharedPreferences с сохраненными запросами
            savedSearches = getSharedPreferences(SEARCHES, MODE_PRIVATE);
            // Получить сохраненные теги в ArrayList и отсортировать их
                   tags = new ArrayList<>(savedSearches.getAll().keySet());
                   Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);
            // Получить ссылку на RecyclerView и настроить его
                   RecyclerView recyclerView =
                      (RecyclerView) findViewById(R.id.recyclerView);
            // Получить LinearLayoutManager для вертикального списка
                   recyclerView.setLayoutManager(new LinearLayoutManager(this));
            // Создать RecyclerView.Adapter для связывания тегов с RecyclerView
                   adapter = new SearchesAdapter(
                      tags, itemClickListener, itemLongClickListener);
                   recyclerView.setAdapter(adapter);
            // Назначить ItemDecorator для рисования линий между элементами
                   recyclerView.addItemDecoration(new ItemDivider(this));
            // Зарегистрировать слушателя для сохранения или редактирования
                   saveFloatingActionButton =
                      (FloatingActionButton) findViewById(R.id.fab);
                   saveFloatingActionButton.setOnClickListener(saveButtonListener);
                   updateSaveFAB(); // Скрыть кнопку, потому что поля EditText пусты
                }
    // Управление состоянием кнопки saveFloatingActionButton
        private final TextWatcher textWatcher = new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count,
              int after) { }
    // Проверка состояния saveFloatingActionButton после ввода данных
           @Override
           public void onTextChanged(CharSequence s, int start, int before,
              int count) {
              updateSaveFAB();
           }
           @Override
           public void afterTextChanged(Editable s) { }
        };
    // Управление видимостью saveFloatingActionButton
        private void updateSaveFAB() {
    // Проверить, присутствуют ли данные в обоих компонентах EditText
           if (queryEditText.getText().toString().isEmpty() ||
              tagEditText.getText().toString().isEmpty())
              saveFloatingActionButton.hide();
           else
              saveFloatingActionButton.show();
        }
    // saveButtonListener сохраняет пару "тег—запрос" в SharedPreferences
        private final OnClickListener saveButtonListener =
           new OnClickListener() {
    // добавить/обновить данные, если оба поля содержат данные
              @Override
              public void onClick(View view) {
                 String query = queryEditText.getText().toString();
                 String tag = tagEditText.getText().toString();
                 if (!query.isEmpty() && !tag.isEmpty()) {
    // Скрыть экранную клавиатуру
                    ((InputMethodManager) getSystemService(
                      Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                          view.getWindowToken(), 0);
                    addTaggedSearch(tag, query); // добавить/обновить запрос
                    queryEditText.setText(""); // очистить queryEditText
                    tagEditText.setText(""); // очистить tagEditText
                    queryEditText.requestFocus(); // queryEditText получает фокус
                 }
              }
           };
    // Добавление нового запроса с обновлением всех кнопок
        private void addTaggedSearch(String tag, String query) {
    // Получение SharedPreferences.Editor для сохранения новой пары
           SharedPreferences.Editor preferencesEditor = savedSearches.edit();
           preferencesEditor.putString(tag, query); // Сохранение текущего запроса
           preferencesEditor.apply(); // Сохранение обновленных настроек
    // Если тег новый, добавить его, отсортировать и вывести список
           if (!tags.contains(tag)) {
              tags.add(tag); // Добавить новый тег
              Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);
              adapter.notifyDataSetChanged(); // Обновление тегов в RecyclerView
           }
        }
    // itemClickListener запускает браузер для вывода результатов поиска
        private final OnClickListener itemClickListener =
           new OnClickListener() {
              @Override
              public void onClick(View view) {
    // Получение строки запроса и создание URL для этого запроса
                 String tag = ((TextView) view).getText().toString();
                 String urlString =  getString(R.string.search_URL) +
                    Uri.encode(savedSearches.getString(tag, ""), "UTF-8");
    // Создание интента для запуска браузера
                 Intent webIntent = new Intent(Intent.ACTION_VIEW,
                   Uri.parse(urlString));
                 startActivity(webIntent); // Вывести результаты в браузере
              }
           };
    // itemLongClickListener отображает диалоговое окно для пересылки,
    // изменения или удаления сохраненного запроса
        private final OnLongClickListener itemLongClickListener =
           new OnLongClickListener() {
              @Override
              public boolean onLongClick(View view) {
    // Получение тега, на котором было сделано длинное нажатие
                 final String tag = ((TextView) view).getText().toString();
    // Создание нового объекта AlertDialog
                 AlertDialog.Builder builder =
                    new AlertDialog.Builder(MainActivity.this);
    // Назначение заголовка AlertDialog
                 builder.setTitle(
                    getString(R.string.share_edit_delete_title, tag));
    // Назначение списка вариантов и создание обработчика
                 builder.setItems(R.array.dialog_items,
                    new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                             switch (which) {
                             case 0: // share
                                shareSearch(tag);
                                break;
                             case 1: // edit
    // Заполнение EditText для тега и запроса
                                tagEditText.setText(tag);
                                queryEditText.setText(
                                   savedSearches.getString(tag, ""));
                                break;
                             case 2: // delete
                                deleteSearch(tag);
                                break;
                          }
                       }
                    }
                 );
    // Назначение негативной кнопки AlertDialog
                 builder.setNegativeButton(getString(R.string.cancel), null);
                 builder.create().show(); // Отображение AlertDialog
                 return true;
              }
           };
    // Выбор приложения для пересылки URL-адреса сохраненного запроса
        private void shareSearch(String tag) {
    // Создание URL-адреса, представляющего поисковый запрос
           String urlString = getString(R.string.search_URL) +
              Uri.encode(savedSearches.getString(tag, ""), "UTF-8");
    // Создание объекта Intent для пересылки
           Intent shareIntent = new Intent();
           shareIntent.setAction(Intent.ACTION_SEND);
           shareIntent.putExtra(Intent.EXTRA_SUBJECT,
              getString(R.string.share_subject));
           shareIntent.putExtra(Intent.EXTRA_TEXT,
              getString(R.string.share_message, urlString));
           shareIntent.setType("text/plain");
    // Вывод списка приложений с возможностью пересылки текста
           startActivity(Intent.createChooser(shareIntent,
              getString(R.string.share_search)));
        }
    // Удаление запроса после подтверждения операции пользователем
        private void deleteSearch(final String tag) {
    // Создание нового объекта AlertDialog и назначение сообщения
           AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
           confirmBuilder.setMessage(getString(R.string.confirm_message, tag));
    // Настройка негативной кнопки (CANCEL)
           confirmBuilder.setNegativeButton(getString(R.string.cancel), null);
    // Настройка позитивной кнопки (DELETE)
           confirmBuilder.setPositiveButton(getString(R.string.delete),
              new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int id) {
                    tags.remove(tag); // Удаление тега из tags
    // Получение SharedPreferences.Editor для удаления запроса
                    SharedPreferences.Editor preferencesEditor =
                       savedSearches.edit();
                    preferencesEditor.remove(tag); // Удаление запроса
                    preferencesEditor.apply(); // Сохранение изменений
    // Повторное связывание для вывода обновленного списка
     adapter.notifyDataSetChanged();
                 }
              }
           );
          confirmBuilder.create().show(); // Отображение AlertDialog
        }
    }

