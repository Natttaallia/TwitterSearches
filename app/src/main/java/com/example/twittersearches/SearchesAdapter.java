package com.example.twittersearches;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SearchesAdapter
        extends RecyclerView.Adapter<SearchesAdapter.ViewHolder> {
    // Слушатели MainActivity, регистрируемые для каждого элемента списка
    private final View.OnClickListener clickListener;
    private final View.OnLongClickListener longClickListener;
// List<String> для хранения данных элементов RecyclerView
    private final List<String> tags; // Поисковые запросы
// Конструктор
    public SearchesAdapter(List<String> tags,
       View.OnClickListener clickListener,
       View.OnLongClickListener longClickListener) {
       this.tags = tags;
       this.clickListener = clickListener;
       this.longClickListener = longClickListener;
    }
// Вложенный субкласс RecyclerView.ViewHolder используется для
// реализации паттерна View-Holder в контексте RecyclerView-
// логики повторного использования представлений
    public static class ViewHolder extends RecyclerView.ViewHolder  {
       public final TextView textView;
// Настройка объекта ViewHolder элемента RecyclerView
       public ViewHolder(View itemView,
          View.OnClickListener clickListener,
          View.OnLongClickListener longClickListener) {
          super(itemView);
          textView = (TextView) itemView.findViewById(R.id.textView);
// Связывание слушателей с itemView
          itemView.setOnClickListener(clickListener);
          itemView.setOnLongClickListener(longClickListener);
       }
    }
// Создает новый элемент списка и его объект ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
// Заполнение макета list_item
       View view = LayoutInflater.from(parent.getContext()).inflate(
          R.layout.list_item, parent, false);
// Создание ViewHolder для текущего элемента
       return (new ViewHolder(view, clickListener, longClickListener));
    }
// Назначение текста элемента списка для вывода тега запроса
    @Override
       public void onBindViewHolder(ViewHolder holder, int position) {
       holder.textView.setText(tags.get(position));
    }
// Возвращение количества элементов, связываемых через адаптер
    @Override
    public int getItemCount() {
       return tags.size();
    }
 }
