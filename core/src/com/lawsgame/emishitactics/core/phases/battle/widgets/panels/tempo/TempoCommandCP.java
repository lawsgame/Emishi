package com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Banner;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.BuildCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.ChangeTactic;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.SwitchWeaponCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.BattleCommandManager;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces.ChoicePanel;

public class TempoCommandCP extends ChoicePanel.CommandChoicePanel {
    private static final int BUTTON_WIDTH = 210;
    private static final int BUTTON_HEIGTH_SW = 70;
    private static final int BUTTON_HEIGTH_SC = 90;
    public static float X_PADDING = 15f;
    public static float Y_REL_PADDING = 10f;
    public static float SLIDE_DURATION = 0.5f;

    private Skin skin;

    public TempoCommandCP(Viewport uiport, Skin skin) {
        super(uiport, SLIDE_DURATION, X_PADDING, Y_REL_PADDING, BUTTON_WIDTH, 0, true, false);
        this.skin = skin;
        this.yPadding = Y_REL_PADDING;
    }

    @Override
    public void setContent(int rowActor, int colActor, BattleCommandManager bcm, Data.ActionChoice param) {
        Array<Button> buttons = new Array<Button>();
        final Array<ActorCommand> commands = bcm.getAvailableCommands(rowActor, colActor, param, true);


        // instanciate buttons

        Button button;
        for(int i = 0; i < commands.size; i++){
            final ActorCommand command = commands.get(i);
            button = createButton(commands.get(i));
            buttons.add(button);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    notifyReceiver(command);
                }
            });
        }

        // set layout

        clear();
        Button b;
        int panelHeight = 0;
        for(int i = 0; i < buttons.size; i++){
            b = buttons.get(i);
            panelHeight += b.getHeight();
            add(b).width(BUTTON_WIDTH).height(b.getHeight()).row();
        }
        setHeight(panelHeight);
        updateY();
    }


    private Button createButton(ActorCommand choice){
        Button button;
        if(choice instanceof SwitchWeaponCommand){

            SwitchWeaponCommand swc  = (SwitchWeaponCommand) choice;
            Weapon nw = swc.getNewWeapon();
            StringBuilder builder = new StringBuilder();
            if(nw != null) {
                builder.append("Name : "+nw.getName());
                builder.append("\n"+nw.getTemplate().getDamageMin()+"-"+nw.getTemplate().getDamageMax());
                builder.append(" | "+nw.getTemplate().getAccuracy());
                builder.append(" | "+nw.getTemplate().getRangeMin()+"-"+nw.getTemplate().getRangeMax());
                builder.append(" | "+nw.getTemplate().getDamageType().name().toLowerCase());
                builder.append(" | "+nw.getDurability()+"/"+nw.getTemplate().getDurabilityMax());
                builder.append("\nAbility : "+nw.getTemplate().getAbility().name().toLowerCase());
            }else{
                builder.append("no data");
            }
            button = new TextButton(builder.toString(), skin, "commandpan");
            button.setHeight(BUTTON_HEIGTH_SW);
        }else if(choice instanceof BuildCommand){

            BuildCommand bc = (BuildCommand)choice;
            button = new TextButton(bc.getBuildingType().getName(), skin, "commandpan");
            button.setHeight(TempoActionCP.BUTTON_HEIGTH);
        }else if(choice instanceof ChangeTactic){

            ChangeTactic ctc = (ChangeTactic)choice;
            StringBuilder builder = new StringBuilder();
            if(ctc.getInitiator() != null){
                Banner banner = ctc.getInitiator().getBanner();
                builder.append("Mode : "+ctc.getMode());
                float bonus;
                for(Data.BannerBonus bb : Data.BannerBonus.values()) {
                    bonus = banner.getValue(bb, ctc.getMode());
                    if (bonus > 0){
                        builder.append("\n     "+bb.name().toLowerCase()+" : "+ ((bonus < 1) ? ((int) (bonus *100))+"%" : (int)bonus));
                    }
                }

            }
            button = new TextButton(builder.toString(), skin, "commandpan");
            button.setHeight(BUTTON_HEIGTH_SC);
        }else{

            button = new TextButton("command std choice", skin, "commandpan");
            button.setHeight(TempoActionCP.BUTTON_HEIGTH);
        }
        return button;
    }


    @Override
    public void setButtonIndex(int buttonIndex) {
        this.yPadding = TempoActionCP.Y_PADDING + TempoActionCP.BUTTON_HEIGTH * buttonIndex + Y_REL_PADDING;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
