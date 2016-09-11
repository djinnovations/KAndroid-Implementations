package com.goldadorn.main.activities.cart;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.utils.TypefaceHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 11-06-2016.
 */
public class PaymentMethodsViewController extends RecyclerView.ViewHolder {

    public interface IPayMethodChangedListener {
        void onPaymentSelected(PaymentMethodsDataObj dataObj);
    }

    //public static int initSelection = 1;

    public enum PaymentMethods {NETBANKING, CREDITCARD, EMI, DEBITCARD, CASH}

    final LinearLayout container;
    ArrayList<PaymentMethodsDataObj> payMethodList;
    ArrayList<PaymentMethodViewHolder> listOfVh = new ArrayList<>();
    private IPayMethodChangedListener paySelectionListener;

    public PaymentMethodsViewController(LinearLayout container, IPayMethodChangedListener paySelectionListener) {
        super(container);
        this.container = container;
        this.paySelectionListener = paySelectionListener;
        lastCheckedItemStack = new Stack<>();
    }

    public void displayMethods(ArrayList<PaymentMethodsDataObj> payMethodList) {
        //this.payMethodList = payMethodList;
        for (PaymentMethodsDataObj pdo : payMethodList) {
            PaymentMethodViewHolder pvh = createItems();
            pvh.bindView(pdo);
        }
    }

    private PaymentMethodViewHolder createItems() {
        PaymentMethodViewHolder pvh = new PaymentMethodViewHolder(LayoutInflater.from(container.getContext())
                .inflate(R.layout.adapter_payment_mode, container, false));
        container.addView(pvh.itemView);
        return pvh;
    }


    private Stack<RadioButton> lastCheckedItemStack;

    class PaymentMethodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.tvMethod)
        TextView tvMethods;
        @Bind(R.id.radioBtn)
        RadioButton rbtn;
        @Bind(R.id.rlCheckPay)
        View rlCheckPay;

        PaymentMethodsDataObj dataObj;

        public PaymentMethodViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            /*tvMethods = (TextView) itemView.findViewById(R.id.tvMethod);
            rbtn = (RadioButton) itemView.findViewById(R.id.radioBtn);
            rlCheckPay = itemView.findViewById(R.id.rlCheckPay);*/
            TypefaceHelper.setFont(tvMethods);
            rlCheckPay.setOnClickListener(this);
        }

        void bindView(PaymentMethodsDataObj dataObj) {
            tvMethods.setText(dataObj.getMethodName());
            rbtn.setChecked(dataObj.flag);
            if (dataObj.flag) {
                lastCheckedItemStack.push(rbtn);
            }
            this.dataObj = dataObj;
        }

        @Override
        public void onClick(View v) {

            if (!lastCheckedItemStack.empty()) {
                Log.d("djcart", "stack not empty:");
                lastCheckedItemStack.pop().setChecked(false);
            }
            rbtn.setChecked(true);
            lastCheckedItemStack.push(rbtn);
            if (paySelectionListener != null) {
                paySelectionListener.onPaymentSelected(dataObj);
            }
        }
    }


    public static class PaymentMethodsDataObj {
        private String methodName;
        private boolean flag;

        public PaymentMethodsDataObj(String methodName, boolean flag) {
            this.methodName = methodName;
            this.flag = flag;
        }

        public String getMethodName() {
            return methodName;
        }

        public boolean isFlag() {
            return flag;
        }
    }
}
