package com.jcedar.visinaas.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcedar.visinaas.R;
import com.jcedar.visinaas.provider.DataContract;

import butterknife.Bind;
import butterknife.ButterKnife;


public class RecyclerCursorAdapterAll extends RecyclerViewCursorAdapter<RecyclerCursorAdapterAll.ResultViewHolder>
        implements View.OnClickListener
{
    private final LayoutInflater layoutInflater;
    private OnItemClickListener onItemClickListener;
    public Context context;

    public RecyclerCursorAdapterAll(final Context context)
    {
        super();

        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ResultViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType)
    {
        final View view = this.layoutInflater.inflate(R.layout.list_n_item_all_student, parent, false);
        view.setOnClickListener(this);

        return new ResultViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(final ResultViewHolder holder, final Cursor cursor)
    {
        holder.bindData(cursor);
    }

     /*
     * View.OnClickListener
     */

    @Override
    public void onClick(final View view)
    {
        if (this.onItemClickListener != null)
        {
            final RecyclerView recyclerView = (RecyclerView) view.getParent();
            final int position = recyclerView.getChildLayoutPosition(view);
            if (position != RecyclerView.NO_POSITION)
            {
                final Cursor cursor = this.getItem(position);
                this.onItemClickListener.onItemClicked(cursor);
            }
        }
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.lblName)
        TextView textViewName;

        @Bind(R.id.lblCourse)
        TextView textViewCourse;

        @Bind(R.id.lblDoB)
        TextView textViewDOB;

        @Bind(R.id.lblSchl)
        TextView textViewSchl;

        Context context;

        public ResultViewHolder(final View itemView, Context context)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.context = context;
        }

        public void bindData(final Cursor cursor)
        {
            String context = this.context.getClass().getSimpleName();

            final String name = cursor.getString(cursor.getColumnIndex(DataContract.Students.NAME));
            this.textViewName.setText(name);

            if ( context.equals("AllStudentDetailsActivity")){
                this.textViewCourse.setVisibility( View.GONE );
            }
            final String course = cursor.getString(cursor.getColumnIndex(DataContract.Students.COURSE));
            this.textViewCourse.setText(course);

            final String dob = cursor.getString(cursor.getColumnIndex(DataContract.Students.DATE_OF_BIRTH));
            this.textViewDOB.setText(dob);

            final String schl = cursor.getString(cursor.getColumnIndex(DataContract.Students.CHAPTER));
            this.textViewSchl.setText("");
            //this.textViewSchl.setText(schl);

        }
    }

    public interface OnItemClickListener
    {
        void onItemClicked(Cursor cursor);
    }
}