import {
  describe,
  it,
  expect,
  vi,
} from "vitest";

import {
  mount,
} from "@vue/test-utils";

import ContactCard from "../ContactCard.vue";


vi.mock(
  "../../services/api",
  () => ({
    default: {
      delete: vi.fn(),
    },
  })
);


const contact = {
  id: 1,
  name: "Sohan Kumar",
  phone_number: "+919876543210",
  email: "sohan@example.com",
  address: "Mumbai, India",
};


describe(
  "ContactCard",
  () => {

    it(
      "renders contact information",
      () => {

        const wrapper = mount(
          ContactCard,
          {
            props: {
              contact,
            },

            global: {
              stubs: {
                RouterLink: true,
              },
            },
          }
        );

        expect(
          wrapper.text()
        ).toContain(
          "Sohan Kumar"
        );

        expect(
          wrapper.text()
        ).toContain(
          "+919876543210"
        );

        expect(
          wrapper.text()
        ).toContain(
          "sohan@example.com"
        );

        expect(
          wrapper.text()
        ).toContain(
          "Mumbai, India"
        );
      }
    );


    it(
      "generates correct initials",
      () => {

        const wrapper = mount(
          ContactCard,
          {
            props: {
              contact,
            },

            global: {
              stubs: {
                RouterLink: true,
              },
            },
          }
        );

        expect(
          wrapper.find(
            ".large-avatar"
          ).text()
        ).toBe("SK");
      }
    );

  }
);