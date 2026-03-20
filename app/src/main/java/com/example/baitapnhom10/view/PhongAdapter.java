package com.example.nhatro.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nhatro.R;
import com.example.nhatro.model.Phong;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PhongAdapter extends RecyclerView.Adapter<PhongAdapter.PhongViewHolder> {

    private List<Phong> danhSachPhong;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
        void onDeleteClick(int position);
    }

    public PhongAdapter(Context context, List<Phong> danhSachPhong, OnItemClickListener listener) {
        this.context = context;
        this.danhSachPhong = danhSachPhong;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_phong, parent, false);
        return new PhongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhongViewHolder holder, int position) {
        Phong phong = danhSachPhong.get(position);

        holder.tvMaPhong.setText(phong.getMaPhong());
        holder.tvTenPhong.setText(phong.getTenPhong());

        // Format giá tiền
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.tvGiaThue.setText(formatter.format(phong.getGiaThue()) + " đ/tháng");

        // Tình trạng + màu badge
        if (phong.isDaThue()) {
            holder.tvTinhTrang.setText("Đã thuê");
            holder.tvTinhTrang.setTextColor(context.getResources().getColor(R.color.red_thue));
            holder.tvTinhTrang.setBackground(context.getResources().getDrawable(R.drawable.bg_badge_red));
            holder.layoutNguoiThueInfo.setVisibility(View.VISIBLE);
            holder.tvNguoiThue.setText(phong.getTenNguoiThue());
            holder.tvSoDienThoai.setText(phong.getSoDienThoai());
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.bg_da_thue));
        } else {
            holder.tvTinhTrang.setText("Còn trống");
            holder.tvTinhTrang.setTextColor(context.getResources().getColor(R.color.green_trong));
            holder.tvTinhTrang.setBackground(context.getResources().getDrawable(R.drawable.bg_badge_green));
            holder.layoutNguoiThueInfo.setVisibility(View.GONE);
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
        }

        // Sự kiện click
        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(position);
            return true;
        });
        holder.btnXoa.setOnClickListener(v -> listener.onDeleteClick(position));
    }

    @Override
    public int getItemCount() {
        return danhSachPhong.size();
    }

    public void capNhatDanhSach(List<Phong> danhSachMoi) {
        this.danhSachPhong = danhSachMoi;
        notifyDataSetChanged();
    }

    // ===== ViewHolder =====
    static class PhongViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvMaPhong, tvTenPhong, tvGiaThue, tvTinhTrang, tvNguoiThue, tvSoDienThoai;
        LinearLayout layoutNguoiThueInfo;
        ImageButton btnXoa;

        public PhongViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView           = itemView.findViewById(R.id.cardView);
            tvMaPhong          = itemView.findViewById(R.id.tvMaPhong);
            tvTenPhong         = itemView.findViewById(R.id.tvTenPhong);
            tvGiaThue          = itemView.findViewById(R.id.tvGiaThue);
            tvTinhTrang        = itemView.findViewById(R.id.tvTinhTrang);
            tvNguoiThue        = itemView.findViewById(R.id.tvNguoiThue);
            tvSoDienThoai      = itemView.findViewById(R.id.tvSoDienThoai);
            layoutNguoiThueInfo = itemView.findViewById(R.id.layoutNguoiThueInfo);
            btnXoa             = itemView.findViewById(R.id.btnXoa);
        }
    }
}