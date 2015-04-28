/*package com.android.jackapp.btantiloss.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomActionBar extends RelativeLayout{

	  private LayoutInflater a;
	  private ViewGroup b;
	  private TextView c;
	  private ImageView d;
	  private c e;
	  private View.OnClickListener f = new b(this);

	  public CustomActionBar(Context paramContext)
	  {
	    super(paramContext);
	    a(paramContext);
	  }

	  public CustomActionBar(Context paramContext, AttributeSet paramAttributeSet)
	  {
	    super(paramContext, paramAttributeSet);
	    a(paramContext);
	  }

	  private void a(Context paramContext)
	  {
	    this.a = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
	    this.b = ((ViewGroup)this.a.inflate(2130903050, null));
	    addView(this.b);
	    this.c = ((TextView)this.b.findViewById(2131361868));
	    this.d = ((ImageView)this.b.findViewById(2131361869));
	    this.d.setOnClickListener(this.f);
	  }

	  public void a(boolean paramBoolean)
	  {
	    if (paramBoolean)
	    {
	      this.d.setVisibility(0);
	      return;
	    }
	    this.d.setVisibility(8);
	  }

	  public void setOnActionBarListener(c paramc)
	  {
	    this.e = paramc;
	  }

	  public void setTitle(Object paramObject)
	  {
	    if ((paramObject instanceof Integer))
	      this.c.setText(((Integer)paramObject).intValue());
	    while (!(paramObject instanceof String))
	      return;
	    this.c.setText(String.valueOf(paramObject));
	  }

}
*/