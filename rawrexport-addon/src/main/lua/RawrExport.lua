local RawrFrame = CreateFrame("Frame", "RawrExportFrame")

-- the internal names for equipement slots, Finger1Slot and Trinket1Slot 
-- commented out to avoid needless duplicatoin of items
local SlotNames = {
    "HeadSlot",
    "NeckSlot",
    "ShoulderSlot",
    "BackSlot",
    "ChestSlot",
    "ShirtSlot",
    "TabardSlot",
    "WristSlot",
    "HandsSlot",
    "WaistSlot",
    "LegsSlot",
    "FeetSlot",
    "Finger0Slot",
    --"Finger1Slot",
    "Trinket0Slot",
    --"Trinket1Slot",
    "MainHandSlot",
    "SecondaryHandSlot",
    "RangedSlot",
    "AmmoSlot"
}

local ItemQualityByName = {
    Poor = 0,
    Common = 1,
    Uncommon = 2,
    Rare = 3,
    Epic = 4,
    Legendary = 5,
    Heirloom = 6
}

-- reverse table for item quality.  1 is added to the index so we can use
-- ipairs to keep them ordered but not lose "poor" as an option
local ItemQualityByNumber = {}
for k,v in pairs(ItemQualityByName) do
    ItemQualityByNumber[v + 1] = k
end

-- options defaults to be used when the addon runs for the fist time or 
-- defaults are reset
local RawrExportOptionsDefaults = {
    ilvl = 200,
    quality = "Epic",
    enchants = true
}

function rawrInit(self, event, ...)
    if arg1 == "RawrExport" then
        if not RawrExportOptions then
            RawrExportOptions = {}
            for k,v in pairs(RawrExportOptionsDefaults) do
                RawrExportOptions[k] = v
            end
        end

        if not RawrExportAvailableItems then
            RawrExportAvailableItems = {}
        end

        local panel = CreateFrame("Frame", "RawrExportOptionsPanel", UIParent)
        panel.name = "RawrExport"

        panel.title = panel:CreateFontString(nil, 'RawrExportOptionsTitleString', 'GameFontNormalLarge')
        panel.title:SetPoint('TOPLEFT', 16, -16)
        panel.title:SetText("RawrExport")
                
        panel.subtitle = panel:CreateFontString(nil, 'RawrExportOptionsTitleSubString', 'GameFontHighlightSmall')
        panel.subtitle:SetHeight(32)
        panel.subtitle:SetPoint('TOPLEFT', panel.title, 'BOTTOMLEFT', 0, -5)
        panel.subtitle:SetPoint('BOTTOMRIGHT', panel, 'TOPRIGHT', -16, -100)
        panel.subtitle:SetNonSpaceWrap(true)
        panel.subtitle:SetJustifyH('LEFT')
        panel.subtitle:SetJustifyV('TOP')
        panel.subtitle:SetText("Exports a filtered set of available gear to be used in the 'Optimize' and 'Evaluate Upgrades' features in Rawr")
             
        panel.quality = CreateFrame('Frame', "RawrExportOptionsQuality", panel, 'UIDropDownMenuTemplate')
        panel.quality:SetPoint('TOPLEFT', panel.subtitle, 'LEFT', -20, -20)
        panel.quality.initialize = function (self, level)
            for k,v in ipairs(ItemQualityByNumber) do
                UIDropDownMenu_AddButton( { 
                    text = v,
                    func = function()
                        UIDropDownMenu_SetText(panel.quality, v)
                    end
                }, level)
            end
        end
        
        panel.quality.text = panel.quality:CreateFontString(nil, 'BACKGROUND')
        panel.quality.text:SetPoint('TOPLEFT', panel.quality, 'TOPLEFT', 20, 10)
        panel.quality.text:SetFontObject('GameFontNormalSmall')
        panel.quality.text:SetText("Item Quality Filter")
        
          
        panel.ilvl = CreateFrame('EditBox', "RawrExportOptionsIlvl", panel, "InputBoxTemplate")
        panel.ilvl:SetAutoFocus(false)
        panel.ilvl:SetNumeric(true)
        panel.ilvl:SetWidth(100)
        panel.ilvl:SetHeight(13)
        panel.ilvl:SetPoint('TOPLEFT', panel.quality, "BOTTOMLEFT", 25, -25)
        panel.ilvl:SetScript('OnShow', function()
            panel.ilvl:SetCursorPosition(0)
        end)
        
        panel.ilvl.text = panel.ilvl:CreateFontString(nil, 'BACKGROUND')
        panel.ilvl.text:SetPoint('TOPLEFT', panel.ilvl, 'TOPLEFT', -5, 15)
        panel.ilvl.text:SetFontObject('GameFontNormalSmall')
        panel.ilvl.text:SetText("Minimum Item Level (iLvl)")
        
        panel.okay = function(self)
            RawrExportOptions.quality = UIDropDownMenu_GetText(panel.quality)
            RawrExportOptions.ilvl = panel.ilvl:GetNumber()
            RawrExportOptions.enchants = (panel.enchants:GetChecked() == 1)
        end
        
        panel.refresh = function(self)
            UIDropDownMenu_SetText(panel.quality, RawrExportOptions.quality)
            panel.ilvl:SetNumber(RawrExportOptions.ilvl)
            panel.enchants:SetChecked(RawrExportOptions.enchants)
        end
        
        panel.default = function(self)
            RawrExportOptions = { }
            for k,v in pairs(RawrExportOptionsDefaults) do
                RawrExportOptions[k] = v
            end
        end
        
        InterfaceOptions_AddCategory(panel)
    end
end

-- register a slash command
SLASH_RAWREXP1 = "/rawrexport"
SLASH_RAWREXP2 = "/re"
SlashCmdList["RAWREXP"] = function(msg)
    if msg == "options" then
        InterfaceOptionsFrame_OpenToCategory("RawrExport")
    elseif msg ~= "" then
        print("RawrExport")
        print("==========")
        print("'/re'", "Perform the export.  If your bank is open, items from your bank will be included.")
        print("'/re options'", "Open the Addon Options")
    else
        DoRawrExport()
    end
end

SLASH_RELOAD1 = "/rl"
SlashCmdList["RELOAD"] = function(msg)
    ReloadUI()
end

function DoRawrExport()
    RawrExportAvailableItems = { }
    
    local itemsFound = 0
    local itemsExported = 0
    
    for arbitraryIndex,slotName in pairs(SlotNames) do
    
        local slotId,texture,checkRelic = GetInventorySlotInfo(slotName)
        local availableItems = GetInventoryItemsForSlot(slotId)

        for location,item in pairs(availableItems) do
            itemsFound = itemsFound + 1
            
            itemName,itemLink,itemQuality,itemLevel = GetItemInfo(item)
            
            if itemLevel >= RawrExportOptions.ilvl and itemQuality >= ItemQualityByName[RawrExportOptions.quality] then
                table.insert(RawrExportAvailableItems, item, true)
                itemsExported = itemsExported + 1
            end
        end
    end

    print("RawrExport Results:")
    print("===================")
    print(itemsFound, "items found")
    print(itemsExported, "items exported")
    print(itemsFound - itemsExported, "items filtered due to ilvl < " .. RawrExportOptions.ilvl .. " or quality < " .. RawrExportOptions.quality)
end

RawrFrame:SetScript("OnEvent", rawrInit)
RawrFrame:RegisterEvent("ADDON_LOADED")
